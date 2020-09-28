package com.pulse.checkoutransparente.service.impl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pulse.checkoutransparente.domain.entity.Cliente;
import com.pulse.checkoutransparente.domain.entity.Endereco;
import com.pulse.checkoutransparente.domain.entity.ItemPedido;
import com.pulse.checkoutransparente.domain.entity.Nota;
import com.pulse.checkoutransparente.domain.entity.Pagamento;
import com.pulse.checkoutransparente.domain.entity.Pedido;
import com.pulse.checkoutransparente.domain.entity.Produto;
import com.pulse.checkoutransparente.domain.entity.Transportadora;
import com.pulse.checkoutransparente.domain.enums.StatusPedido;
import com.pulse.checkoutransparente.domain.repository.Clientes;
import com.pulse.checkoutransparente.domain.repository.Enderecos;
import com.pulse.checkoutransparente.domain.repository.ItemsPedido;
import com.pulse.checkoutransparente.domain.repository.Pedidos;
import com.pulse.checkoutransparente.domain.repository.Produtos;
import com.pulse.checkoutransparente.domain.repository.Transportadoras;
import com.pulse.checkoutransparente.exception.EstadoPedidoInvalidoException;
import com.pulse.checkoutransparente.exception.PedidoNaoEncontradoException;
import com.pulse.checkoutransparente.exception.RegraNegocioException;
import com.pulse.checkoutransparente.rest.dto.ItemPedidoDTO;
import com.pulse.checkoutransparente.rest.dto.PedidoDTO;
import com.pulse.checkoutransparente.service.PedidoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final Pedidos repository;
    private final Clientes clientesRepository;
    private final Produtos produtosRepository;
    private final ItemsPedido itemsPedidoRepository;
    private final Enderecos enderecosRepository;
    private final Transportadoras transportadorasRepository;
    private final NotaService nota;

    @Override
    @Transactional
    public Pedido salvar( PedidoDTO dto ) {
        Integer idCliente = dto.getCliente();
        Cliente cliente = clientesRepository
                .findById(idCliente)
                .orElseThrow(() -> new RegraNegocioException("Código de cliente inválido."));

        Pedido pedido = new Pedido();
        pedido.setTotal(dto.getTotal());
        pedido.setDataPedido(LocalDate.now());
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.REALIZADO);

        List<ItemPedido> itemsPedido = converterItems(pedido, dto.getItems());
        repository.save(pedido);
        itemsPedidoRepository.saveAll(itemsPedido);
        pedido.setItens(itemsPedido);
        return pedido;
    }

    @Override
    public Optional<Pedido> obterPedidoCompleto(Integer id) {
        return repository.findByIdFetchItens(id);
    }

    @Override
    @Transactional
    public void atualizaStatus( Integer id, StatusPedido statusPedido ) {
        repository
                .findById(id)
                .map( pedido -> {
                    pedido.setStatus(statusPedido);
                    return repository.save(pedido);
                }).orElseThrow(() -> new PedidoNaoEncontradoException() );
    }

    private List<ItemPedido> converterItems(Pedido pedido, List<ItemPedidoDTO> items){
        if(items.isEmpty()){
            throw new RegraNegocioException("Não é possível realizar um pedido sem items.");
        }

        return items
                .stream()
                .map( dto -> {
                    Integer idProduto = dto.getProduto();
                    Produto produto = produtosRepository
                            .findById(idProduto)
                            .orElseThrow(
                                    () -> new RegraNegocioException(
                                            "Código de produto inválido: "+ idProduto
                                    ));

                    ItemPedido itemPedido = new ItemPedido();
                    itemPedido.setQuantidade(dto.getQuantidade());
                    itemPedido.setVlrTot(dto.getVlrTot());
                    itemPedido.setVlrUni(dto.getVlrUni());
                    itemPedido.setPedido(pedido);
                    itemPedido.setProduto(produto);
                    return itemPedido;
                }).collect(Collectors.toList());

    }
    
    public Pedido buscarOuFalhar(Integer pedidoId) {
    	return this.repository.findByIdCodPed(pedidoId)
    			.orElseThrow(() -> new PedidoNaoEncontradoException());
    }
    

    private Pedido buscarPedidoEChecarStatus(Integer pedidoId, StatusPedido ...permitidos) {
    	
    	Pedido pedido = this.buscarOuFalhar(pedidoId);
    	StatusPedido atual = pedido.getStatus();
    	
    	if (Arrays.stream(permitidos).anyMatch(x -> x.equals(atual))) {
    		return pedido;
    	}
    	throw new EstadoPedidoInvalidoException(atual, permitidos);
    }

    public Pedido selecionarEndereco(Integer pedidoId, Endereco endereco) {
    	Pedido pedido = this.buscarPedidoEChecarStatus(pedidoId, 
    			StatusPedido.REALIZADO,
    			StatusPedido.ENDERECO_SELECIONADO); 
    	
         endereco = enderecosRepository
                .findById(endereco.getId())
                .orElseThrow(() -> new RegraNegocioException("Código de endereço inválido."));
    	
    	pedido.setEnderecoEntrega(endereco);
    	pedido.setStatus(StatusPedido.ENDERECO_SELECIONADO);
    	
    	return this.repository.save(pedido);
	}
    
    public Pedido selecionarTransportadora (Integer pedidoId, Transportadora transportadora) {
    	
    	Pedido pedido = this.buscarPedidoEChecarStatus(pedidoId, 
    			StatusPedido.ENDERECO_SELECIONADO,
    			StatusPedido.TRANSPORTADORA_SELECIONADA);
    	
    	if (pedido.getItens().isEmpty()) {
    		throw new EstadoPedidoInvalidoException(String.format(
    				"O pedido de código %d está vazio, não é possível selecionar uma transportadora", 
    				pedidoId));
    	}
       	
    	transportadora = transportadorasRepository
                .findById(transportadora.getId())
                .orElseThrow(() -> new RegraNegocioException("Código de transportadora inválido."));
    	
    	pedido.setTransportadora(transportadora);
    	pedido.setStatus(StatusPedido.TRANSPORTADORA_SELECIONADA);
    	pedido.setFrete(transportadora.getTaxaFrete());
    	
    	return this.repository.save(pedido);
    }
    
    public Pedido executarPagamento (Integer pedidoId, Pagamento pagamento ) {
    	
    	Pedido pedido = this.buscarPedidoEChecarStatus(pedidoId, 
    			StatusPedido.TRANSPORTADORA_SELECIONADA);
    	
    	pedido.setTotal(pagamento.getValor());
    	pedido.setPagamento(pagamento);
    	
    	Nota nota = this.nota.gerarNotaFiscal(pedido);

    	//TODO O Objetivo aqui é setar a nota e pegar o número dela para retornar o numero de rastreio
    	//     porem esta dando um erro tem que corrigir
//    	pedido.setNota(nota);
    	pedido.setStatus(StatusPedido.PAGAMENTO_CONFIRMADO);
    	
    	return this.repository.save(pedido);
    }
}
