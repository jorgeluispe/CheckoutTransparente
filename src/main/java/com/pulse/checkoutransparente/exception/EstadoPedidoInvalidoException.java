package com.pulse.checkoutransparente.exception;

import java.util.Arrays;

import com.pulse.checkoutransparente.domain.enums.StatusPedido;

public class EstadoPedidoInvalidoException extends NegocioException {

	private static final long serialVersionUID = 1L;
	
	public EstadoPedidoInvalidoException(StatusPedido atual, StatusPedido ...permitidos) {
		this(String.format("operação inválida para o status atual (%s) do pedido. os status permitidos para a operação são: %s", 
				atual.toString(), 
				Arrays.toString(permitidos)
		));
	}
	
	public EstadoPedidoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
