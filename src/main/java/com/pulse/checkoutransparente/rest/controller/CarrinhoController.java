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

import com.pulse.checkoutransparente.domain.entity.Carrinho;
import com.pulse.checkoutransparente.domain.entity.ItemCarrinho;
import com.pulse.checkoutransparente.domain.enums.StatusPedido;
import com.pulse.checkoutransparente.rest.dto.AtualizacaoStatusPedidoDTO;
import com.pulse.checkoutransparente.rest.dto.CarrinhoDTO;
import com.pulse.checkoutransparente.rest.dto.InformacaoItemCarrinhoDTO;
import com.pulse.checkoutransparente.rest.dto.InformacoesCarrinhoDTO;
import com.pulse.checkoutransparente.service.CarrinhoService;

@RestController
@RequestMapping("/api/carrinhos")
public class CarrinhoController {

    private CarrinhoService service;

    public CarrinhoController(CarrinhoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Integer save( @RequestBody @Valid CarrinhoDTO dto ){
        Carrinho carrinho = service.salvar(dto);
        return carrinho.getId();
    }

    @GetMapping("{id}")
    public InformacoesCarrinhoDTO getById( @PathVariable Integer id ){
        return service
                .obterCarrinhoCompleto(id)
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

    private InformacoesCarrinhoDTO converter(Carrinho carrinho){
        return InformacoesCarrinhoDTO
                .builder()
                .codigo(carrinho.getId())
                .dataPedido(carrinho.getDataPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .cpf(carrinho.getCliente().getCpf())
                .nomeCliente(carrinho.getCliente().getNome())
                .total(carrinho.getTotal())
                .status(carrinho.getStatus().name())
                .items(converter(carrinho.getItens()))
                .build();
    }

    private List<InformacaoItemCarrinhoDTO> converter(List<ItemCarrinho> itens){
        if(CollectionUtils.isEmpty(itens)){
            return Collections.emptyList();
        }
        return itens.stream().map(
                item -> InformacaoItemCarrinhoDTO
                            .builder().descricaoProduto(item.getProduto().getDescricao())
                            .precoUnitario(item.getProduto().getPreco())
                            .quantidade(item.getQuantidade())
                            .build()
        ).collect(Collectors.toList());
    }
}
