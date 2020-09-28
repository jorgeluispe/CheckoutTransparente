package com.pulse.checkoutransparente.service;

import java.util.Optional;

import com.pulse.checkoutransparente.domain.entity.Carrinho;
import com.pulse.checkoutransparente.domain.enums.StatusPedido;
import com.pulse.checkoutransparente.rest.dto.CarrinhoDTO;

public interface CarrinhoService {
    Carrinho salvar( CarrinhoDTO dto );
    Optional<Carrinho> obterCarrinhoCompleto(Integer id);
    void atualizaStatus(Integer id, StatusPedido statusPedido);

}
