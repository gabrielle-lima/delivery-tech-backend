package com.deliverytech.delivery_api.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.deliverytech.delivery_api.dto.request.ItemPedidoRequest;
import com.deliverytech.delivery_api.model.Pedido;
import com.deliverytech.delivery_api.model.StatusPedido;

public interface PedidoService {


    Pedido criar(Pedido pedido);
    Pedido buscarPorId(Long id);
    List<Pedido> buscarPorCliente(Long clienteId);
    List<Pedido> buscarPorRestaurante(Long restauranteId);
    List<Pedido> buscaPorStatus(StatusPedido status);
    Pedido atualizarStatus(Long id, StatusPedido pedidoStatus);
    Pedido confirmar(Long id);
    Pedido cancelar(Long id);

        Pedido adicionarItem(Long pedidoId, Long produtoId, Integer quantidade);

    BigDecimal calcularTotal(Pedido pedido);
    BigDecimal calcularTotalPedido(List<ItemPedidoRequest> itens);


    List<Pedido> listarComFiltros(StatusPedido status, LocalDate dataInicio, LocalDate dataFim);

    List<Pedido> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim);

    Optional<Pedido> buscarPorIdComItens(Long id);
    
    List<Pedido> buscarPorClienteComItens(Long clienteId);
    List<Pedido> listarTodos();
    void deletar(Long id);
}
