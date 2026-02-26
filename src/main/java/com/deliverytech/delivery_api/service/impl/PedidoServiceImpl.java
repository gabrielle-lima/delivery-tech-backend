package com.deliverytech.delivery_api.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliverytech.delivery_api.dto.request.ItemPedidoRequest;
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

    @Override
    public BigDecimal calcularTotal(Pedido pedido) {
        if(pedido.getItens() == null || pedido.getItens().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return pedido.getItens().stream().map(item -> item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> buscarPorRestaurante(Long restauranteId) {
        return pedidoRepository.findByRestauranteId(restauranteId);
    }

    @Override
    public Pedido cancelar(Long pedidoId) {
        Pedido pedidoACancelar = pedidoRepository.findById(pedidoId)
        .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        if(pedidoACancelar.getStatus() == StatusPedido.ENTREGUE)  {
            throw new RuntimeException("Não é possível cancelar um pedido já entregue");
        }

        if(pedidoACancelar.getStatus() == StatusPedido.CANCELADO) {
         throw new RuntimeException("Pedido já está cancelado");
        }
        Pedido pedidoCancelado = pedidoRepository.save(pedidoACancelar);
        log.info("Pedido cancelado: {}", pedidoId);
        return pedidoCancelado;
    }

   @Override
    @Transactional(readOnly = true)
    public List<Pedido> buscarPorStatus(StatusPedido statusPedido) {
        return pedidoRepository.findByStatus(statusPedido);
    }


    @Override
    @Transactional(readOnly = true)
    public List<Pedido> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return pedidoRepository.findByDataPedidoBetween(inicio, fim);
    }

    @Override
    @Transactional(readOnly = true) 
    public List<Pedido> buscarTodos() {
        return pedidoRepository.findAll();
    }


    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalPedido(List<ItemPedido> itens) {
        log.info("Calculando total do pedido com {} itens", itens.size());
        if(itens == null || itens.isEmpty()) {
           log.warn("Lista de itens vazia, retornando total zero");
           return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;

        for(ItemPedidoRequest itemRequest:itens) {
            Produto produto = produtoRepository.findById(itemRequest.getProdutoId())
            .orElseThrow(() -> new RuntimeException("Produto não encontrado: {}", itemRequest.getProdutoId()));

            if(!produto.getAtivo()) {
             throw new RuntimeException("Produto não está disponível:  ", itemRequest.getProdutoId());

            }

            BigDecimal precoUnitario = produto.getPreco();
            BigDecimal quantidade = BigDecimal.valueOf(itemRequest.getQuantidade());
            BigDecimal subTotal = precoUnitario.multiply(quantidade);

            total = total.add(subTotal);
            log.debug("Item calculado: Produto {} , Preco: {} , Subtotal: {}", produto.getNome(), itemRequest.getQuantidade(),precoUnitario, subTotal);

        }
        log.info("Total calculado: R$ {}", total);
        return total;
    }

@Override
@Transactional
public void deletar(Long id) {
    Pedido pedido = pedidoRepository.findById(id)
    .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

    pedidoRepository.delete(pedido);
    log.info("Pedido deletado - {} ", pedido.getId());

}

 @Override
    @Transactional(readOnly = true)
    public List<Pedido> listarComFiltros(StatusPedido status, LocalDate dataInicio, LocalDate dataFim) {
        log.info("Listando pedidos com filtros - Status: {}, Data início: {}, Data fim: {}", status, dataInicio, dataFim);
        
        // Se nenhum filtro foi fornecido, retorna todos
        if (status == null && dataInicio == null && dataFim == null) {
            return pedidoRepository.findAll();
        }
        
        // Converter LocalDate para LocalDateTime para comparação
        LocalDateTime inicioDateTime = dataInicio != null ? dataInicio.atStartOfDay() : null;
        LocalDateTime fimDateTime = dataFim != null ? dataFim.atTime(23, 59, 59) : null;
        
        // Se apenas status foi fornecido
        if (status != null && inicioDateTime == null && fimDateTime == null) {
            return pedidoRepository.findByStatus(status);
        }
        
        // Se apenas período foi fornecido
        if (status == null && inicioDateTime != null && fimDateTime != null) {
            return pedidoRepository.findByDataPedidoBetween(inicioDateTime, fimDateTime);
        }
        
        // Se status e período foram fornecidos
        if (status != null && inicioDateTime != null && fimDateTime != null) {
            return pedidoRepository.findByStatusAndDataPedidoBetween(status, inicioDateTime, fimDateTime);
        }
        
        // Casos parciais (apenas dataInicio ou apenas dataFim)
        if (inicioDateTime != null && fimDateTime == null) {
            return pedidoRepository.findByDataPedidoGreaterThanEqual(inicioDateTime);
        }
        
        if (inicioDateTime == null && fimDateTime != null) {
            return pedidoRepository.findByDataPedidoLessThanEqual(fimDateTime);
        }
        
        return pedidoRepository.findAll();
    }

}
