package com.pulse.checkoutransparente.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pulse.checkoutransparente.domain.entity.Carrinho;
import com.pulse.checkoutransparente.domain.entity.Cliente;

public interface Carrinhos extends JpaRepository<Carrinho, Integer> {

    List<Carrinho> findByCliente(Cliente cliente);

    @Query(" select p from Carrinho p left join fetch p.itens where p.id = :id ")
    Optional<Carrinho> findByIdFetchItens(@Param("id") Integer id);
}
