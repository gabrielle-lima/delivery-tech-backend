package com.deliverytech.delivery_api.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliverytech.delivery_api.model.ItemPedido;
import com.deliverytech.delivery_api.model.Pedido;
import com.deliverytech.delivery_api.model.Produto;
import com.deliverytech.delivery_api.model.StatusPedido;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.service.PedidoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;

    @Override
    public Pedido criar(Pedido pedido) {
        pedido.setStatus(StatusPedido.CRIADO);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setValorTotal(BigDecimal.ZERO);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        log.info("Pedido criado com sucesso - ID: {}", pedidoSalvo.getId());
        return pedidoSalvo;
    }

    @Override
    @Transactional(readOnly = true)
    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> buscarPorCliente(Long id) {
        return pedidoRepository.findByClienteId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> buscarPorClienteComItens(Long clienteId) {
        return pedidoRepository.findByClienteIdWithItems(clienteId);
    }

    @Override
    public Pedido adicionarItem(Long pedidoId, Long produtoId, Integer quantidade) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        ItemPedido item = ItemPedido.builder()
                .pedido(pedido).produto(produto).quantidade(quantidade)
                .precoUnitario(produto.getPreco())
                .build();

        if (pedido.getItens() == null) {
            pedido.setItens(new ArrayList<>());
        }
        pedido.getItens().add(item);
        BigDecimal novoTotal = calcularTotal(pedido);  //TBI
        pedido.setValorTotal(novoTotal);
        return pedidoRepository.save(pedido);
    }

    @Override
    public Pedido confirmar(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
         .orElseThrow(() ->  new RuntimeException("Pedido não encontrado"));
         pedido.setStatus(StatusPedido.CONFIRMADO);
         return pedidoRepository.save(pedido);
    }

    @Override
    @Transactional
    public Pedido atualizarStatus(Long pedidoId, StatusPedido novoStatus) {
        log.info("Atualizando status do pedido {} para {}", pedidoId, novoStatus);
        Pedido pedido =  pedidoRepository.findById(pedidoId)
        .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        log.info("Status atual do pedido {} : {}", pedidoId, pedido.getStatus());

        pedido.setStatus(novoStatus);

        Pedido salvo = pedidoRepository.save(pedido);

        log.info("Status do pedido: {} atualizado com sucesso para {}", pedidoId, novoStatus);

        return salvo;

    }

}
