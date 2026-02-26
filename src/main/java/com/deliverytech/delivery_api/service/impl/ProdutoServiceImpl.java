package com.deliverytech.delivery_api.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.deliverytech.delivery_api.model.Produto;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.service.ProdutoService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProdutoServiceImpl implements  ProdutoService {

    private final ProdutoRepository produtoRepository;

    @Override
    public Produto cadastrar(Produto produto) {
        validarPreco(produto.getPreco());
        if (produto.getDisponivel() == null) {
            produto.setDisponivel(true);

        }
        return produtoRepository.save(produto);
    }

    @Override
    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    @Override
    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    @Override
    public Produto atualizar(Long id, Produto atualizado) {
        return produtoRepository.findById(id)
                .map(produto -> {
                    if (atualizado.getPreco() != null) {
                        validarPreco(atualizado.getPreco());
                        produto.setPreco(atualizado.getPreco());
                    }
                    if (atualizado.getNome() != null) {
                        produto.setNome(atualizado.getNome());
                    }
                    if (atualizado.getDescricao() != null) {
                        produto.setDescricao(atualizado.getDescricao());
                    }
                    if (atualizado.getCategoria() != null) {
                        produto.setCategoria(atualizado.getCategoria());
                    }
                    return produtoRepository.save(atualizado);
                }).orElseThrow(() -> new RuntimeException("Produto não encontrado"));

    }

    @Override
    public void deletar(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new RuntimeException("Produto não encontrado: {}" + id);
        }

        produtoRepository.deleteById(id);

        log.info("Produto deletado:  {}", id);
    }

    @Override
    public void inativar(Long id) {
        produtoRepository.findById(id)
                .ifPresentOrElse(produto -> {
                    produto.setDisponivel(false);
                    produtoRepository.save(produto);
                    log.info("Produto inativado:  {}", id);
                },
                        () -> {
                            throw new RuntimeException("Produto não encontrado:  {} " + id);
                        });
    }

    @Override
    public List<Produto> buscarPorRestaurante(Long restauranteId) {
        return produtoRepository.findByRestaurante_id(restauranteId);
    }

    @Override
    public List<Produto> buscarPorCategoria(String categoria) {
        return produtoRepository.findByCategoria(categoria);
    }

    @Override
    public List<Produto> listarDisponiveis() {
        return produtoRepository.findByDisponivelTrue();
    }

    @Override
    public void alterarDisponibilidade(Long id, boolean disponivel) {
        produtoRepository.findById(id)
                .ifPresentOrElse(produto -> {
                    produto.setDisponivel(disponivel);
                    produtoRepository.save(produto);
                },
                        () -> {
                            throw new RuntimeException("Produto não encontrado");
                        });
    }

    @Override
    public void validarPreco(BigDecimal preco) {
        if (preco == null) {
            throw new IllegalArgumentException("Preco não pode ser nulo");
        }
        if (preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preco deve ser maior que zero");
        }
        BigDecimal precoMaximo = new BigDecimal("99999.99");

        if (preco.compareTo(precoMaximo) > 0) {
            throw new IllegalArgumentException("Preço não pode ser superior a R$ 99.999,99");

        }
    }

    @Override
    public List<Produto> buscarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return List.of();
        }
        return produtoRepository.findByNomeContainingIgnoreCase(nome.trim());
    }

}
