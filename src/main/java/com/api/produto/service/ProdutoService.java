package com.api.produto.service;

import com.api.produto.model.Produto;

import java.util.List;

public interface ProdutoService {

    List<Produto> listarTodos();

    Produto buscarPorId(Long id);

    Produto salvar(Produto produto);

    Produto atualizar(Long id, Produto produto);

    void deletar(Long id);
}
