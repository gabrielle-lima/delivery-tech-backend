package com.deliverytech.delivery_api.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.deliverytech.delivery_api.dto.request.RestauranteRequest;
import com.deliverytech.delivery_api.model.Restaurante;

public interface RestauranteService {

    Restaurante cadastrar(RestauranteRequest restauranteRequest);
    Optional<Restaurante> buscarPorId(Long id);
    List<Restaurante> listarTodos();
    List<Restaurante> listarAtivos();
    List<Restaurante> buscarPorCategoria(String categoria);
    List<Restaurante> buscarPorAvaliacao(BigDecimal minAvaliacao);
    List<Restaurante> buscarPorTaxaEntrega(BigDecimal maxTaxa);
    Restaurante alterarStatus(Long id, Boolean ativo);
    Restaurante atualizar(Long id, RestauranteRequest restauranteRequest);
    List<Restaurante> buscarProximos(String cep);
    List<Restaurante> listarComFiltros(String categoria, Boolean ativo);
    void inativar(Long id);



}
