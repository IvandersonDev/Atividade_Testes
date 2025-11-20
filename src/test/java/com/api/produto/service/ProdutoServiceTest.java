package com.api.produto.service;

import com.api.produto.service.ValidacaoException;
import com.api.produto.model.Categoria;
import com.api.produto.model.Produto;
import com.api.produto.repository.CategoriaRepository;
import com.api.produto.repository.ProdutoRepository;
import com.api.produto.service.impl.ProdutoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProdutoServiceImpl produtoService;

    private Categoria categoria;
    private Produto produto;

    @BeforeEach
    void setUp() {
        categoria = new Categoria(1L, "Tecnologia");
        produto = new Produto(null, "Notebook", BigDecimal.valueOf(2500), 5, categoria);
    }

    @Test
    void deveSalvarProdutoComSucesso() {
        Produto salvo = new Produto(1L, "Notebook", BigDecimal.valueOf(2500), 5, categoria);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(produtoRepository.save(any(Produto.class))).thenReturn(salvo);

        Produto resultado = produtoService.salvar(produto);

        assertEquals(1L, resultado.getId());
        assertEquals("Notebook", resultado.getNome());
        verify(produtoRepository).save(any(Produto.class));
    }

    @Test
    void deveLancarExcecaoQuandoPrecoInvalido() {
        produto.setPreco(BigDecimal.ZERO);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> produtoService.salvar(produto));
        assertTrue(exception.getMessage().contains("Preço"));
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaNaoExistir() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ValidacaoException.class, () -> produtoService.salvar(produto));
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    void deveRetornarTodosOsProdutos() {
        Produto segundo = new Produto(2L, "Mouse", BigDecimal.valueOf(100), 15,
                new Categoria(2L, "Periféricos"));
        when(produtoRepository.findAll()).thenReturn(Arrays.asList(
                new Produto(1L, "Notebook", BigDecimal.valueOf(2500), 5, categoria),
                segundo
        ));

        List<Produto> produtos = produtoService.listarTodos();

        assertEquals(2, produtos.size());
        assertEquals("Mouse", produtos.get(1).getNome());
    }

    @Test
    void deveAtualizarProdutoExistente() {
        Produto existente = new Produto(1L, "Notebook", BigDecimal.valueOf(2000), 4, categoria);
        Produto atualizado = new Produto(null, "Notebook Gamer", BigDecimal.valueOf(3500), 2, categoria);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(produtoRepository.save(existente)).thenReturn(existente);

        Produto resultado = produtoService.atualizar(1L, atualizado);

        assertEquals("Notebook Gamer", resultado.getNome());
        assertEquals(BigDecimal.valueOf(3500), resultado.getPreco());
        verify(produtoRepository).save(existente);
    }

    @Test
    void deveDeletarProdutoComSucesso() {
        Produto existente = new Produto(1L, "Notebook", BigDecimal.valueOf(2000), 4, categoria);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(existente));

        produtoService.deletar(1L);

        verify(produtoRepository).delete(existente);
    }
}
