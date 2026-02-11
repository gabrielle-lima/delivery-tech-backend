package com.deliverytech.delivery_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.deliverytech.delivery_api.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Cliente> findByAtivoTrue();
    List<Cliente> findByNomeContainingIgnoreCase(String nome);



    @Query(value = "SELECT c.nome, COUNT(p.id) as total_pedidos " + 
        "FROM CLIENTE c " +
        "LEFT JOIN PEDIDO p ON c.id = p.cliente_id " +
        "GROUP BY c.id, c.nome " +
        "ORDER BY total_pedidos DESC " +
        "LIMIT 10", nativeQuery = true)
        List<Object[]> rankingClientesPorPedidos();
}
