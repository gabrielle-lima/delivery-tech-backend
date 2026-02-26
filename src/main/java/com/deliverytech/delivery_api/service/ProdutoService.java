package com.deliverytech.delivery_api.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.deliverytech.delivery_api.model.Produto;

public interface ProdutoService {

    Produto cadastrar(Produto produto);                        
    Optional<Produto> buscarPorId(Long id);                   
    List<Produto> listarTodos();                              
    Produto atualizar(Long id, Produto produtoAtualizado);    
    void inativar(Long id);                                   
    void deletar(Long id);
    
    List<Produto> buscarPorRestaurante(Long restauranteId);   
    List<Produto> buscarPorCategoria(String categoria);       
    List<Produto> listarDisponiveis();
    List<Produto> buscarPorNome(String nome);

    
    void alterarDisponibilidade(Long id, boolean disponivel); 
    void validarPreco(BigDecimal preco);

}
