package com.deliverytech.delivery_api.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.deliverytech.delivery_api.model.Pedido;
import com.deliverytech.delivery_api.model.StatusPedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long>{

    List<Pedido> findByClienteId(Long clienteId);

    List<Pedido> findByRestauranteId(Long restauranteId);

    List<Pedido> findByStatus(StatusPedido status);

    List<Pedido> findByDataPedidoBetween(LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT p FROM PEDIDO p LEFT JOIN FETCH p.items  i LEFT JOIN FETCH i.produto WHERE p.id = :id")
    List<Pedido> findByClienteIdWithItems(@Param("clienteId") Long clienteId);

    @Query("SELECT p.restaurante.nome, SUM(p.valorTotal) " + 
    "FROM PEDIDO p " + 
    "GROUP BY p.restaurante.nome " +
    "ORDER BY SUM(p.valorTotal) DESC" )
    List<Object[]> calcularTotalVendasRestaurante();

    @Query("SELECT p FROM PEDIDO p WHERE p.valorTotal  > :valor  ORDER BY p.valorTotal DESC ")
    List<Pedido> buscarPedidosComValorAcimaDe(@Param("valor") BigDecimal valor);

    @Query("SELECT p from PEDIDO p " + "WHERE p.dataPedido  BETWEEEN :inicio AND :fim" + "AND p.status  = :status" + "ORDER BY dataPedido DESC")
    List<Pedido> relatorioPedidosPorPeriodoEStatus(@Param ("inicio") 
    LocalDateTime inicio, 
    @Param("fim")
    LocalDateTime fim, 
    @Param("status") StatusPedido status);


    @Query("SELECT p.restaurante,nome as nomeRestaurante, " + 
        "SUM(p.valorTotal) as totalVendas, " +
        "COUNT(p.id) as quantidadePedidos " +
        "FROM PEDIDO p " +
        "GROUP BY p.restaurante.nome"
    )
    List<RelatorioVendas> obterRelatorioVendasPorRestaurante();

    List<Pedido> findByStatusAndDataPedidoBetween(StatusPedido status, LocalDateTime inicio, LocalDateTime fim);

    List<Pedido> findByDataPedidoGreaterThanEqual(LocalDateTime data);

    List<Pedido> findByDataPedidoLessThanEqual(LocalDateTime dataFim);

}
