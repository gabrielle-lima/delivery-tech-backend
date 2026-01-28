package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.model.Cliente;
import com.deliverytech.delivery.repository.ClienteRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteRepository clienteRepository;

    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }


    @GetMapping
    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    @PostMapping
    public Cliente criar(@RequestBody Cliente cliente) {
     return clienteRepository.save(cliente);
    }
}
