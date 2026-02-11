package com.deliverytech.delivery_api.service.impl;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliverytech.delivery_api.dto.request.ClienteRequest;
import com.deliverytech.delivery_api.model.Cliente;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.service.ClienteService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;


    @Override
    public Cliente cadastrar(ClienteRequest clienteRequest) {
        log.info("Iniciando cadastro de cliente: {}", clienteRequest.getEmail());

        Cliente cliente = new Cliente();
        cliente.setNome(clienteRequest.getNome());
        cliente.setEmail(clienteRequest.getEmail());
        cliente.setTelefone(clienteRequest.getTelefone());
        cliente.setEndereco(clienteRequest.getEndereco());


        if(clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado:    "  + cliente.getEmail());
        }
    

        validarDadosCliente(cliente);
        cliente.setAtivo(true);

        Cliente clienteSalvo = clienteRepository.save(cliente);
        log.info("Cliente salvo com sucesso - ID: {}", clienteSalvo.getId());
        return clienteSalvo;

    }

        @Override
        @Transactional(readOnly = true)
        public Optional<Cliente> buscarPorId(Long id) {
            log.debug("Buscando cliente por ID: {}", id);
            return clienteRepository.findById(id);
        }


        @Override
        @Transactional(readOnly = true)
        public Optional<Cliente> buscarPorEmail(String email) {
            log.debug("Buscando cliente por email:  {}", email);
            return clienteRepository.findByEmail(email);
        }

        @Override
        @Transactional(readOnly = true)
        public List<Cliente> listarAtivos() {
            log.debug("Listando clientes ativos");
            return clienteRepository.findByAtivoTrue();
        }

        @Override
        @Transactional(readOnly = true)
        public List<Cliente> buscarPorNome(String nome) {
            log.debug("Buscar clientes por nome: {}", nome);
            return clienteRepository.findByNomeContainingIgnoreCase(nome);
        }

        @Override
        public Cliente atualizar(Long id, ClienteRequest clienteRequest) {
            log.info("Atualizando cliente ID: {}", id);
            Cliente cliente = buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com ID: " + id));

            if(!cliente.getEmail().equals(clienteRequest.getEmail()) && clienteRepository.existsByEmail(clienteRequest.getEmail())) {
                throw new IllegalArgumentException("Email já cadastrado: " + clienteRequest.getEmail());
            }

            Cliente clienteParaValidacao = new Cliente();
            clienteParaValidacao.setNome(clienteRequest.getNome());
            clienteParaValidacao.setTelefone(clienteRequest.getTelefone());
            clienteParaValidacao.setEndereco(clienteRequest.getEndereco());


            validarDadosCliente(clienteParaValidacao);

            cliente.setNome(clienteRequest.getNome());
            cliente.setEmail(clienteRequest.getEmail());
            cliente.setTelefone(clienteRequest.getTelefone());
            cliente.setEndereco(clienteRequest.getEndereco());

            Cliente clienteSalvo = clienteRepository.save(cliente);
            log.info("Cliente atualizado com sucesso - ID: {}", clienteSalvo.getId());
            return clienteSalvo;
        }

        @Override
        public void inativar(Long id) {
            log.info("Inativando cliente ID: {}", id);
            Cliente cliente = buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com ID:  " + id));

            if(!cliente.getAtivo()) {
                throw new IllegalArgumentException("Cliente já está inativo:  " + id);
            }

            try {
                cliente.inativar();
            } catch(Exception e) {
                cliente.setAtivo(false);
            }

            clienteRepository.save(cliente);
            log.info("Cliente inativado com sucesso  - ID: {}", id);
        }

        @Override
        public Cliente ativarDesativarCliente(Long id) {
            log.info("Alterando status do cliente ID: {}", id);
            Cliente cliente = buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado  " + id));
            cliente.setAtivo(!cliente.getAtivo());

            Cliente clienteSalvo = clienteRepository.save(cliente);
            log.info("Status do cliente alterado para:  {} - ID {}", clienteSalvo.getAtivo(), clienteSalvo.getId());
            return clienteSalvo;

        }

        private void validarDadosCliente(Cliente cliente) {
           if(cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
           }

           if(cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
           }

           if(cliente.getNome().length() <  2) {
            throw new IllegalArgumentException("Nome deve ter pelo menos 2 caracteres");
           }

           if(cliente.getNome().length() > 100) {
            throw new IllegalArgumentException("Nome não pode ter mais de 100 caracteres");
           }

           if(!cliente.getEmail().contains("@") || !cliente.getEmail().contains(".")) {
            throw new IllegalArgumentException("Email deve ter formato válido");
           }

           
           if(cliente.getEmail().length() > 150) {
            throw new IllegalArgumentException("Email não pode ter mais de 150 caracteres");
           }
           log.debug("Validações de negócio aprovadas para cliente:  {} ", cliente.getEmail());
        }

}
