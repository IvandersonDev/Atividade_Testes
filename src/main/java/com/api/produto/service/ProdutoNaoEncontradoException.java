package com.api.produto.service;

public class ProdutoNaoEncontradoException extends RuntimeException {

    public ProdutoNaoEncontradoException(Long id) {
        super("Produto com id " + id + " não encontrado");
    }
}
