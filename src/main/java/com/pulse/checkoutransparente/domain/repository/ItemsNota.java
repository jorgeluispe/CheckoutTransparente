package com.pulse.checkoutransparente.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pulse.checkoutransparente.domain.entity.ItemNota;

public interface ItemsNota extends JpaRepository<ItemNota, Integer> {
	
}
