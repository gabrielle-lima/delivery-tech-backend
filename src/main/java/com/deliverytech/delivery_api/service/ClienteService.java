package com.deliverytech.delivery_api.service;

import java.util.List;
import java.util.Optional;

import com.deliverytech.delivery_api.dto.request.ClienteRequest;
import com.deliverytech.delivery_api.model.Cliente;

public interface ClienteService {

    Cliente cadastrar(ClienteRequest clienteRequest);

    Optional<Cliente> buscarPorId(Long id);

    Optional<Cliente> buscarPorEmail(String email);

    List<Cliente> listarAtivos();

    List<Cliente> buscarPorNome(String nome);

    Cliente atualizar(Long id, ClienteRequest clienteRequest);

    void inativar(Long id);

    Cliente  ativarDesativarCliente(Long id);

}
