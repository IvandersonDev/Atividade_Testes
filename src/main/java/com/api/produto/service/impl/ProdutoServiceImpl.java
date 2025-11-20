package com.api.produto.service.impl;

import com.api.produto.service.ProdutoNaoEncontradoException;
import com.api.produto.service.ValidacaoException;
import com.api.produto.model.Categoria;
import com.api.produto.model.Produto;
import com.api.produto.repository.CategoriaRepository;
import com.api.produto.repository.ProdutoRepository;
import com.api.produto.service.ProdutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoServiceImpl implements ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    @Override
    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    @Override
    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(id));
    }

    @Override
    public Produto salvar(Produto produto) {
        validarDadosObrigatorios(produto);
        Categoria categoria = buscarCategoria(produto);
        produto.setCategoria(categoria);
        return produtoRepository.save(produto);
    }

    @Override
    public Produto atualizar(Long id, Produto produto) {
        Produto existente = buscarPorId(id);
        validarDadosObrigatorios(produto);
        Categoria categoria = buscarCategoria(produto);

        existente.setNome(produto.getNome());
        existente.setPreco(produto.getPreco());
        existente.setQuantidade(produto.getQuantidade());
        existente.setCategoria(categoria);

        return produtoRepository.save(existente);
    }

    @Override
    public void deletar(Long id) {
        Produto existente = buscarPorId(id);
        produtoRepository.delete(existente);
    }

    private void validarDadosObrigatorios(Produto produto) {
        if (!StringUtils.hasText(produto.getNome())) {
            throw new ValidacaoException("Nome do produto é obrigatório");
        }

        BigDecimal preco = produto.getPreco();
        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacaoException("Preço deve ser maior que zero");
        }

        Integer quantidade = produto.getQuantidade();
        if (quantidade == null || quantidade < 0) {
            throw new ValidacaoException("Quantidade deve ser positiva");
        }

        if (produto.getCategoria() == null) {
            throw new ValidacaoException("Categoria é obrigatória");
        }
    }

    private Categoria buscarCategoria(Produto produto) {
        Long categoriaId = produto.getCategoria().getId();
        if (categoriaId == null) {
            throw new ValidacaoException("Categoria é obrigatória");
        }

        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ValidacaoException("Categoria informada não existe"));
    }
}
