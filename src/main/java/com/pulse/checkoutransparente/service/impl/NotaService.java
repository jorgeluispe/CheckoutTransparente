package com.pulse.checkoutransparente.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pulse.checkoutransparente.domain.entity.ItemNota;
import com.pulse.checkoutransparente.domain.entity.ItemPedido;
import com.pulse.checkoutransparente.domain.entity.Nota;
import com.pulse.checkoutransparente.domain.entity.Pedido;
import com.pulse.checkoutransparente.domain.repository.Notas;

@Service
@Transactional
public class NotaService {

	@Autowired
	private Notas notaRepository;

	private BigDecimal AliquotaFixa = new BigDecimal("0.65");

	public Nota gerarNotaFiscal(Pedido pedido) {

		Nota nota = new Nota();
		nota.setPedido(pedido);
		nota.setNomeDestinatario(pedido.getCliente().getNome());
		nota.setCpfDestinatario(pedido.getCliente().getCpf());

		nota.setEndereco(pedido.getEnderecoEntrega().getLogradouro());

		nota.setSubtotal(pedido.getSubtotal());
		nota.setTaxaFrete(pedido.getFrete());
		nota.setValorTotal(pedido.getTotal());

		List<ItemNota> itensNota = pedido.getItens().stream().map(x -> gerarItemFiscal(x, nota))
				.collect(Collectors.toList());

		nota.setItens(itensNota);

		return this.notaRepository.save(nota);
	}

	private ItemNota gerarItemFiscal(ItemPedido itemPedido, Nota nota) {
		ItemNota item = new ItemNota();
		item.setNotaFiscal(nota);

		item.setImposto(itemPedido.getVlrTot().multiply(this.AliquotaFixa));

		item.setDescricaoProduto(itemPedido.getProduto().getDescricao());
		item.setPrecoUnitario(itemPedido.getVlrUni());
		item.setQuantidade(itemPedido.getQuantidade());
		item.setPrecoTotal(itemPedido.getVlrTot());

		return item;
	}
}
