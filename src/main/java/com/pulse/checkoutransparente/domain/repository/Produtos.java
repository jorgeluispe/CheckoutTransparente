package com.pulse.checkoutransparente.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pulse.checkoutransparente.domain.entity.Produto;

public interface Produtos extends JpaRepository<Produto,Integer> {
}
