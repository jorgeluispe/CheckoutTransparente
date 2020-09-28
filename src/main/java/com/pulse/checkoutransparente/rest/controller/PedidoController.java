package com.pulse.checkoutransparente.rest.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pulse.checkoutransparente.domain.entity.Endereco;
import com.pulse.checkoutransparente.domain.entity.ItemPedido;
import com.pulse.checkoutransparente.domain.entity.Pagamento;
import com.pulse.checkoutransparente.domain.entity.Pedido;
import com.pulse.checkoutransparente.domain.entity.Transportadora;
import com.pulse.checkoutransparente.domain.enums.StatusPedido;
import com.pulse.checkoutransparente.exception.NegocioException;
import com.pulse.checkoutransparente.rest.dto.AtualizacaoStatusPedidoDTO;
import com.pulse.checkoutransparente.rest.dto.InformacaoItemPedidoDTO;
import com.pulse.checkoutransparente.rest.dto.InformacoesPedidoDTO;
import com.pulse.checkoutransparente.rest.dto.PedidoDTO;
import com.pulse.checkoutransparente.service.PedidoService;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Integer save( @RequestBody @Valid PedidoDTO dto ){
        Pedido pedido = service.salvar(dto);
        return pedido.getId();
    }

    @GetMapping("{id}")
    public InformacoesPedidoDTO getById( @PathVariable Integer id ){
        return service
                .obterPedidoCompleto(id)
                .map( p -> converter(p) )
                .orElseThrow(() ->
                        new ResponseStatusException(NOT_FOUND, "Pedido não encontrado."));
    }

    @PatchMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    public void updateStatus(@PathVariable Integer id ,
                             @RequestBody AtualizacaoStatusPedidoDTO dto){
        String novoStatus = dto.getNovoStatus();
        service.atualizaStatus(id, StatusPedido.valueOf(novoStatus));
    }

    @PostMapping("/{id}/endereco")
    public Pedido selecionarEndereco(@PathVariable Integer id, 
    		@RequestBody Endereco endereco) {
    	try {
    		return this.service.selecionarEndereco(id, endereco);
    	} catch (Exception e) {
    		throw new NegocioException(e.getMessage());
    	}
	}
    
    @PostMapping("/{id}/transportadora")
    public Pedido selecionarTransportadora(@PathVariable Integer id, 
    		@RequestBody Transportadora transportadora) {
    	try {
    		return this.service.selecionarTransportadora(id, transportadora);
    	} catch (Exception e) {
    		throw new NegocioException(e.getMessage());
    	}
	}
    
    @PostMapping("/{id}/executar-pagamento")
    public Pedido executarPagamento(@PathVariable Integer id, 
    		@RequestBody Pagamento pagamento) {
    	try {
    		return this.service.executarPagamento(id, pagamento);
    	} catch (Exception e) {
    		throw new NegocioException(e.getMessage());
    	}
	}
    
    private InformacoesPedidoDTO converter(Pedido pedido){
        return InformacoesPedidoDTO
                .builder()
                .codigo(pedido.getId())
                .dataPedido(pedido.getDataPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .cpf(pedido.getCliente().getCpf())
                .nomeCliente(pedido.getCliente().getNome())
                .total(pedido.getTotal())
                .status(pedido.getStatus().name())
                .items(converter(pedido.getItens()))
                .build();
    }

    private List<InformacaoItemPedidoDTO> converter(List<ItemPedido> itens){
        if(CollectionUtils.isEmpty(itens)){
            return Collections.emptyList();
        }
        return itens.stream().map(
                item -> InformacaoItemPedidoDTO
                            .builder().descricaoProduto(item.getProduto().getDescricao())
                            .precoUnitario(item.getProduto().getPreco())
                            .quantidade(item.getQuantidade())
                            .vlrUni(item.getVlrUni())
                            .vlrTot(item.getVlrTot())
                            .build()
        ).collect(Collectors.toList());
    }
}
