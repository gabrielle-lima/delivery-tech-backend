package com.deliverytech.delivery_api.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deliverytech.delivery_api.dto.request.ClienteRequest;
import com.deliverytech.delivery_api.dto.response.ApiResponseWrapper;
import com.deliverytech.delivery_api.model.Cliente;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.service.ClienteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteRepository clienteRepository;

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<?> cadastrar(@Valid @RequestBody ClienteRequest clienteRequest) {
        try {
            log.info("Recebida requisição para cadastrar cliente: {}", clienteRequest.getEmail());
            Cliente clienteSalvo = clienteService.cadastrar(clienteRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
        } catch (IllegalArgumentException e) {
            log.warn("Erro de validação ao cadastrar cliente: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erro:  " + e.getMessage());
        } catch (Exception e) {
            log.warn("Erro ao cadastrar cliente", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listar() {
        log.info("Recebida requisição para listar ativos");
        List<Cliente> clientes = clienteService.listarAtivos();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<Cliente>> buscarPorId(@PathVariable Long id) {
        log.info("Recebida requisição para buscar cliente por ID: {}", id);
        Optional<Cliente> cliente = clienteService.buscarPorId(id);
        if (cliente.isPresent()) {
            return ResponseEntity.ok(ApiResponseWrapper.success(cliente.get(), "Cliente encontrado com sucesso"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponseWrapper.error("Cliente não encontrado"));
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponseWrapper<Cliente>> buscarPorEmail(@PathVariable String email) {
        log.info("Recebida requisição para buscar cliente por email: {}", email);
        Optional<Cliente> cliente = clienteService.buscarPorEmail(email);
        if (cliente.isPresent()) {
            return ResponseEntity.ok(ApiResponseWrapper.success(cliente.get(), "Cliente encontrado com sucesso"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/buscar")
    public ResponseEntity<List<Cliente>> buscarPorNome(@RequestParam String nome) {
        log.info("Recebida requisição para buscar clientes por nome: {}", nome);
        List<Cliente> cliente = clienteService.buscarPorNome(nome);
        return ResponseEntity.ok(cliente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody ClienteRequest clienteRequest) {
        try {
            log.info("Recebida requisição para atualizar cliente - ID {} : ", id);
            Cliente clienteAtualizado = clienteService.atualizar(id, clienteRequest);
            return ResponseEntity.ok(clienteAtualizado);
        } catch (IllegalArgumentException e) {
            log.warn("Erro de validação ao atualizar cliente - ID {} : ", e.getMessage());
            return ResponseEntity.badRequest().body("Erro:   " + e.getMessage());
        } catch (Exception e ) {
            log.error("Erro ao atualizar cliente - ID {} : ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> inativar(@PathVariable Long id) {

    try {
        log.info("Recebida requisição para inativar cliente - ID {} : ", id);
        clienteService.inativar(id);
        return ResponseEntity.ok().body("Cliente inativado com sucesso");
    } catch (IllegalArgumentException e) {
        log.warn("Erro ao inativar cliente: {}", e.getMessage());
        return ResponseEntity.badRequest().body("Erro  " + e.getMessage());
    } catch (Exception e) {
         log.error("Erro interno ao inativar cliente", e);
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor"); 
    }

    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> ativarDesativarCliente(@PathVariable Long id) {
        try {
        log.info("Recebida requisição para alterar status do cliente - ID {} :", id);
        Cliente clienteAtualizado = clienteService.ativarDesativarCliente(id);
        String status = clienteAtualizado.getAtivo() ? "ativado" : "desativado";
        return ResponseEntity.ok().body(Map.of("mensagem", "Cliente" + status + " com sucesso", "cliente", clienteAtualizado));
        } catch(IllegalArgumentException e) {
          log.warn("Erro ao alterar status do cliente {} :  ", e.getMessage());
          return ResponseEntity.badRequest().body("Erro  " + e.getMessage());
        } catch(Exception e) {
            log.error("Erro interno ao alterar status do cliente", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Erro interno do servidor");
        }

    }


      
}

