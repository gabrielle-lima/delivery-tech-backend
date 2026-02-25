package com.deliverytech.delivery_api.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliverytech.delivery_api.dto.request.RestauranteRequest;
import com.deliverytech.delivery_api.model.Restaurante;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import com.deliverytech.delivery_api.service.RestauranteService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RestauranteServiceImpl implements RestauranteService {

    private final RestauranteRepository restauranteRepository;

    @Override
    public Restaurante cadastrar(RestauranteRequest restauranteRequest) {
        log.info("Iniciando cadastro de restaurante: {}", restauranteRequest.getNome());
        Restaurante restaurante = new Restaurante();
        restaurante.setNome(restauranteRequest.getNome());
        restaurante.setCategoria(restauranteRequest.getCategoria());
        restaurante.setTaxaEntrega(restauranteRequest.getTaxaEntrega());
        restaurante.setTelefone(restauranteRequest.getTelefone());
        restaurante.setAtivo(true);

        Restaurante salvo = restauranteRepository.save(restaurante);
        log.info("Restaurante cadastrado com sucesso: {}", salvo.getId());
        return salvo;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Restaurante> buscarPorId(Long id) {
        return restauranteRepository.findById(id);

    }

    @Override
    @Transactional(readOnly = true)
    public List<Restaurante> listarTodos() {
        return restauranteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Restaurante> listarAtivos() {
        return restauranteRepository.findByAtivoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Restaurante> buscarPorCategoria(String categoria) {
        return restauranteRepository.findByCategoria(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Restaurante> buscarPorAvaliacao(BigDecimal minAvaliacao) {
        return restauranteRepository.findByAvaliacaoGreaterThanEqual(minAvaliacao);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Restaurante> buscarPorTaxaEntrega(BigDecimal maxTaxa) {
        return restauranteRepository.findByTaxaEntregaLessThanEqual(maxTaxa);
    }

    @Override
    public void inativar(Long id) {
        restauranteRepository.findById(id)
                .ifPresentOrElse(restaurante -> {
                    restaurante.setAtivo(false);
                    restauranteRepository.save(restaurante);
                },
                        () -> {
                            throw new RuntimeException("Restaurante não encontrado - ID: " + id);
                        });
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTaxaEntrega(Long restauranteId, String cep) {
        log.info("Calculando taxa de entrega - Restaurante ID: {}, CEP: {}", restauranteId, cep);
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new RuntimeException("Restaurante não encontrado = ID: " + restauranteId));

        if (!restaurante.getAtivo()) {
            throw new RuntimeException("Restaurante não está disponível para a entrega");
        }

        BigDecimal taxaBase = restaurante.getTaxaEntrega();

        String primeirosDigitos = cep.substring(0, Math.min(2, cep.length()));

        try {
            int codigoRegiao = Integer.parseInt(primeirosDigitos);

            BigDecimal multiplicador;
            if (codigoRegiao == 1) {
                multiplicador = BigDecimal.ONE;
            } else if(codigoRegiao >= 2 && codigoRegiao <= 5) {
                multiplicador =  new BigDecimal("1.20");
            } else if(codigoRegiao >= 6 && codigoRegiao <= 9) {
                multiplicador = new BigDecimal("1.50");
            } else {
              multiplicador =   new BigDecimal("2.00");
            }

            BigDecimal taxaFinal = taxaBase.multiply(multiplicador).setScale(2, BigDecimal.ROUND_HALF_UP);
            log.info("Taxa calculada: R$ (base: R$ {}, multiplicador: {})", taxaFinal, taxaBase, multiplicador);
            return taxaFinal;
        } catch (Exception e) {
         log.warn("CEP inválido: {}, usando taxaBase", cep);
         return taxaBase;
        }

    }

    @Override
    public Restaurante alterarStatus(Long id, Boolean ativo) {
        log.info("Alterando status do restaurante ID: {} para: {}", id, ativo); 
        Restaurante restaurante = restauranteRepository.findById(id).orElseThrow(() -> new RuntimeException("Restaurante não encontrado"));
        restaurante.setAtivo(ativo);
        Restaurante salvo = restauranteRepository.save(restaurante);
        log.info("Status do restaurante {} alterado para {}", id, ativo);
        return salvo;
    } 

    @Override
    @Transactional(readOnly = true)
    public List<Restaurante> buscarProximos(String cep) {
    log.info("Buscando restaurantes próximos ao CEP: {}", cep);
    List<Restaurante> restaurantesAtivos = restauranteRepository.findByAtivoTrue();

    String primeirosDigitos = cep.substring(0, (Math.min(2, cep.length())));

    try {
        int codigoRegiao = Integer.parseInt(primeirosDigitos);
        if(codigoRegiao <= 5 ) {
            log.info("Encontrados {} restaurantes proximos ao CEP {}", restaurantesAtivos.size(), cep);
            return restaurantesAtivos;
        } else {
           List<Restaurante> restaurantesProximos = restaurantesAtivos.stream()
           .filter(r -> r.getTaxaEntrega().compareTo(new BigDecimal("10.00")) 
           <= 0).toList();
           log.info("Encontrados {} restaurantes próximos ao CEP {} (região distante)",
            restaurantesProximos.size(),
            cep
           );
           return restaurantesProximos;
        }
    } catch(NumberFormatException e) {
      log.warn("CEP inválido: {}, retornando todos os restaurantes ativos", cep);
      return restaurantesAtivos;
    }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Restaurante> listarComFiltros(String categoria, Boolean ativo) {
        if(categoria == null && ativo == null) {
            return restauranteRepository.findAll();
        }

        if(categoria != null && ativo == null ) {
            return restauranteRepository.findByCategoria(categoria);
        }
        if(categoria == null && ativo != null) {
            return ativo ? restauranteRepository.findByAtivoTrue() : restauranteRepository.findByAtivoFalse();
        }
        return restauranteRepository.findByCategoriaAndAtivo(categoria, ativo);
    }

    @Override
    public Restaurante atualizar(Long id, RestauranteRequest atualizado) {
        return restauranteRepository.findById(id)
        .map(
            r ->  {
            r.setNome(atualizado.getNome());
            r.setTelefone(atualizado.getTelefone());
            r.setTaxaEntrega(atualizado.getTaxaEntrega());
            r.setTempoEntregaMinutos(atualizado.getTempoEntregaMinutos());
            return restauranteRepository.save(r);
        }).orElseThrow(() ->  new RuntimeException("Restaurante não encontrado"));
    }



}