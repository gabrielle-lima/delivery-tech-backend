package com.deliverytech.delivery.repository;

import com.deliverytech.delivery.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
