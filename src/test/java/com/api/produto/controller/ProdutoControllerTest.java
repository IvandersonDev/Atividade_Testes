package com.api.produto.controller;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.api.produto.model.Categoria;
import com.api.produto.model.Produto;
import com.api.produto.service.ProdutoService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = ProdutoController.class)
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProdutoService produtoService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Categoria categoria = new Categoria(1L, "Tecnologia");

    @Test
    void deveRetornar201AoSalvarProduto() throws Exception {
        Produto produto = new Produto(null, "Notebook", BigDecimal.valueOf(3500), 3, categoria);
        Produto salvo = new Produto(1L, "Notebook", BigDecimal.valueOf(3500), 3, categoria);

        when(produtoService.salvar(any(Produto.class))).thenReturn(salvo);

        mockMvc.perform(MockMvcRequestBuilders.post("/produtos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(produto))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Notebook"));

        verify(produtoService).salvar(any(Produto.class));
    }

    @Test
    void deveRetornarErroQuandoServiceLancarExcecao() throws Exception {
        Produto produto = new Produto(null, "Notebook", BigDecimal.valueOf(3500), 3, categoria);
        when(produtoService.salvar(any(Produto.class))).thenThrow(new RuntimeException("Falha"));

        mockMvc.perform(MockMvcRequestBuilders.post("/produtos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(produto))
                )
                .andExpect(status().is5xxServerError());
    }

    @Test
    void deveRetornar200AoBuscarProdutoExistente() throws Exception {
        Produto produto = new Produto(1L, "Notebook", BigDecimal.valueOf(3500), 3, categoria);
        when(produtoService.buscarPorId(1L)).thenReturn(produto);

        mockMvc.perform(MockMvcRequestBuilders.get("/produtos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Notebook"));
    }

    @Test
    void deveRetornar200EListaDeProdutos() throws Exception {
        when(produtoService.listarTodos()).thenReturn(Arrays.asList(
                new Produto(1L, "Notebook", BigDecimal.valueOf(3500), 3, categoria),
                new Produto(2L, "Mouse", BigDecimal.valueOf(100), 10, categoria)
        ));

        mockMvc.perform(MockMvcRequestBuilders.get("/produtos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].nome").value("Mouse"));
    }

    @Test
    void deveAtualizarProdutoComSucesso() throws Exception {
        Produto atualizado = new Produto(1L, "Notebook Gamer", BigDecimal.valueOf(4500), 2, categoria);

        when(produtoService.atualizar(eq(1L), any(Produto.class))).thenReturn(atualizado);

        mockMvc.perform(MockMvcRequestBuilders.put("/produtos/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(atualizado))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Notebook Gamer"));

        verify(produtoService).atualizar(eq(1L), any(Produto.class));
    }

    @Test
    void deveRetornar204AoDeletarProduto() throws Exception {
        doNothing().when(produtoService).deletar(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/produtos/1"))
                .andExpect(status().isNoContent());

        verify(produtoService).deletar(1L);
    }
}
