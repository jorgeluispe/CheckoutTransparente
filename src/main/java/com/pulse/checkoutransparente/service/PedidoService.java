package com.pulse.checkoutransparente.service;

import java.util.Optional;

import com.pulse.checkoutransparente.domain.entity.Endereco;
import com.pulse.checkoutransparente.domain.entity.Pagamento;
import com.pulse.checkoutransparente.domain.entity.Pedido;
import com.pulse.checkoutransparente.domain.entity.Transportadora;
import com.pulse.checkoutransparente.domain.enums.StatusPedido;
import com.pulse.checkoutransparente.rest.dto.PedidoDTO;

public interface PedidoService {
    Pedido salvar( PedidoDTO dto );
    Optional<Pedido> obterPedidoCompleto(Integer id);
    void atualizaStatus(Integer id, StatusPedido statusPedido);
    Pedido selecionarEndereco(Integer pedidoId, Endereco endereco);
    Pedido selecionarTransportadora (Integer pedidoId, Transportadora transportadora);
    Pedido executarPagamento (Integer pedidoId, Pagamento pagamento);
}
