package com.pulse.checkoutransparente.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pulse.checkoutransparente.domain.entity.Carrinho;
import com.pulse.checkoutransparente.domain.entity.Cliente;
import com.pulse.checkoutransparente.domain.entity.ItemCarrinho;
import com.pulse.checkoutransparente.domain.entity.Produto;
import com.pulse.checkoutransparente.domain.enums.StatusPedido;
import com.pulse.checkoutransparente.domain.repository.Carrinhos;
import com.pulse.checkoutransparente.domain.repository.Clientes;
import com.pulse.checkoutransparente.domain.repository.ItemsCarrinho;
import com.pulse.checkoutransparente.domain.repository.Produtos;
import com.pulse.checkoutransparente.exception.PedidoNaoEncontradoException;
import com.pulse.checkoutransparente.exception.RegraNegocioException;
import com.pulse.checkoutransparente.rest.dto.CarrinhoDTO;
import com.pulse.checkoutransparente.rest.dto.ItemCarrinhoDTO;
import com.pulse.checkoutransparente.service.CarrinhoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CarrinhoServiceImpl implements CarrinhoService {

    private final Carrinhos repository;
    private final Clientes clientesRepository;
    private final Produtos produtosRepository;
    private final ItemsCarrinho itemsCarrinhoRepository;

    @Override
    @Transactional
    public Carrinho salvar( CarrinhoDTO dto ) {
        Integer idCliente = dto.getCliente();
        Cliente cliente = clientesRepository
                .findById(idCliente)
                .orElseThrow(() -> new RegraNegocioException("Código de cliente inválido."));

        Carrinho carrinho = new Carrinho();
        carrinho.setTotal(dto.getTotal());
        carrinho.setDataPedido(LocalDate.now());
        carrinho.setCliente(cliente);
        carrinho.setStatus(StatusPedido.REALIZADO);

        List<ItemCarrinho> itemsCarrinho = converterItems(carrinho, dto.getItems());
        repository.save(carrinho);
        itemsCarrinhoRepository.saveAll(itemsCarrinho);
        carrinho.setItens(itemsCarrinho);
        return carrinho;
    }

    @Override
    public Optional<Carrinho> obterCarrinhoCompleto(Integer id) {
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

    private List<ItemCarrinho> converterItems(Carrinho carrinho, List<ItemCarrinhoDTO> items){
        if(items.isEmpty()){
            throw new RegraNegocioException("Não é possível realizar um carrinho sem items.");
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

                    ItemCarrinho itemCarrinho = new ItemCarrinho();
                    itemCarrinho.setQuantidade(dto.getQuantidade());
                    itemCarrinho.setCarrinho(carrinho);
                    itemCarrinho.setProduto(produto);
                    return itemCarrinho;
                }).collect(Collectors.toList());

    }
}
