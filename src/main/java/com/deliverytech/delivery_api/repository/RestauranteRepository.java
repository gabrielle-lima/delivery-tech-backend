package com.deliverytech.delivery_api.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.deliverytech.delivery_api.model.Restaurante;

public interface RestauranteRepository extends JpaRepository<Restaurante, Long>{

    List<Restaurante> findByCategoria(String categoria);
    List<Restaurante> findByAtivoTrue();
    List<Restaurante> findByAvaliacaoGreaterThanEqual(BigDecimal avaliacao);
    List<Restaurante> findByTaxaEntregaLessThanEqual(BigDecimal taxa);
    
     List<Restaurante> findByAtivoFalse();


    @Query("SELECT r.nome as nomeRestaurante, "
    + " SUM(p.valorTotalVendas) as totalVendas, "
    + "COUNT(p.id) as quantidadePedidos, "
    + "FROM Restaurante r " +
    "LEFT JOIN Pedido p ON r.id = p.restaurante.id " +
    "GROUP BY r.id r.nome"
    )
    List<RelatorioVendas> relatorioVendasPorRestaurante();
    List<Restaurante> findByCategoriaAndAtivo(String categoria, Boolean ativo);


    

}
