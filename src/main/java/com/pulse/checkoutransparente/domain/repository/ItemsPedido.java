package com.pulse.checkoutransparente.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pulse.checkoutransparente.domain.entity.ItemPedido;

public interface ItemsPedido extends JpaRepository<ItemPedido, Integer> {
}
