package com.pulse.checkoutransparente.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pulse.checkoutransparente.domain.entity.Pagamento;

public interface Pagamentos extends JpaRepository<Pagamento,Integer> {
}
