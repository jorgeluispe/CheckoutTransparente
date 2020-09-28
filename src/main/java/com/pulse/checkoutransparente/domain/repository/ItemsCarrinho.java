package com.pulse.checkoutransparente.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pulse.checkoutransparente.domain.entity.ItemCarrinho;

public interface ItemsCarrinho	extends JpaRepository<ItemCarrinho, Integer> {
	
}