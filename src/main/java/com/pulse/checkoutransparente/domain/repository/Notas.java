package com.pulse.checkoutransparente.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pulse.checkoutransparente.domain.entity.Nota;

public interface Notas extends JpaRepository<Nota, Integer> {
	
}
