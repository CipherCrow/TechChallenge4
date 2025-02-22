package br.com.techchallenge.safedeliver.gerenciamentoprodutos.controller;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.com.techchallenge.safedeliver.gerenciamentoprodutos.domain.model.entities.Produto;
import br.com.techchallenge.safedeliver.gerenciamentoprodutos.dto.ProdutoDTO;
import br.com.techchallenge.safedeliver.gerenciamentoprodutos.exception.RegistroNotFoundException;
import br.com.techchallenge.safedeliver.gerenciamentoprodutos.mapper.ProdutoMapper;
import br.com.techchallenge.safedeliver.gerenciamentoprodutos.service.ProdutoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ProdutoControllerTest {

   /* @Mock
    private ProdutoService produtoService;

    private MockMvc mockMvc;

    private AutoCloseable openMocks;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Produto produto;
    private ProdutoDTO produtoDTO;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        ProdutoController produtoController = new ProdutoController(produtoService);
        mockMvc = MockMvcBuilders.standaloneSetup(produtoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        produto = new Produto(1L, "Descricao", 10, 100.0, false);
        produtoDTO = ProdutoMapper.toDTO(produto);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    @DisplayName("POST /produto/criar")
    class CriarEndpointTests {

        @Test
        @DisplayName("Deve criar o produto com sucesso")
        void criarProdutoComSucesso() throws Exception {
            when(produtoService.criar(any(Produto.class))).thenReturn(produto);

            String jsonContent = objectMapper.writeValueAsString(produtoDTO);

            mockMvc.perform(post("/produto/criar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonContent))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(produto.getId()))
                    .andExpect(jsonPath("$.descricao").value(produto.getDescricao()))
                    .andExpect(jsonPath("$.estoque").value(produto.getEstoque()))
                    .andExpect(jsonPath("$.preco").value(produto.getPreco()))
                    .andExpect(jsonPath("$.deletado").value(produto.isDeletado()));
        }
    }

    @Nested
    @DisplayName("PUT /produto/atualizar/{id}")
    class AtualizarEndpointTests {

        @Test
        @DisplayName("Deve atualizar o produto com sucesso")
        void atualizarProdutoComSucesso() throws Exception {
            Produto produtoAtualizado = new Produto(1L, "Nova Descricao", 20, 200.0, false);
            ProdutoDTO novoDTO = ProdutoMapper.toDTO(produtoAtualizado);

            when(produtoService.atualizar(any(Produto.class), eq(1L)))
                    .thenReturn(produtoAtualizado);

            String jsonContent = objectMapper.writeValueAsString(novoDTO);

            mockMvc.perform(put("/produto/atualizar/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonContent))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(produtoAtualizado.getId()))
                    .andExpect(jsonPath("$.descricao").value(produtoAtualizado.getDescricao()))
                    .andExpect(jsonPath("$.estoque").value(produtoAtualizado.getEstoque()))
                    .andExpect(jsonPath("$.preco").value(produtoAtualizado.getPreco()))
                    .andExpect(jsonPath("$.deletado").value(produtoAtualizado.isDeletado()));
        }

        @Test
        @DisplayName("Deve tratar RegistroNotFoundException e retornar NOT_FOUND")
        void atualizarProdutoNotFound() throws Exception {
            ProdutoDTO novoDTO = new ProdutoDTO(1L, "Nova Descricao", 20, 200.0, false);

            when(produtoService.atualizar(any(Produto.class), eq(1L)))
                    .thenThrow(new RegistroNotFoundException("Produto"));

            String jsonContent = objectMapper.writeValueAsString(novoDTO);

            mockMvc.perform(put("/produto/atualizar/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonContent))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Produto não encontrado com este ID!"));
        }
    }

    @Nested
    @DisplayName("PUT /produto/atualizarQuantidade/{id}")
    class AtualizarQuantidadeEndpointTests {

        @Test
        @DisplayName("Deve atualizar a quantidade com sucesso")
        void atualizarQuantidadeComSucesso() throws Exception {
            int novaQuantidade = 50;
            Produto produtoAtualizado = new Produto(1L, "Descricao", novaQuantidade, 100.0, false);

            when(produtoService.atualizarQuantidade(1L, novaQuantidade))
                    .thenReturn(produtoAtualizado);

            mockMvc.perform(put("/produto/atualizarQuantidade/{id}", 1L)
                            .param("quantidadeNova", String.valueOf(novaQuantidade)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.estoque").value(novaQuantidade));
        }

        @Test
        @DisplayName("Deve tratar exceção e retornar BAD_REQUEST")
        void atualizarQuantidadeComException() throws Exception {
            when(produtoService.atualizarQuantidade(1L, 50))
                    .thenThrow(new NullPointerException("ID não pode ser nulo"));

            mockMvc.perform(put("/produto/atualizarQuantidade/{id}", 1L)
                            .param("quantidadeNova", "50"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("ID não pode ser nulo"));
        }
    }

    @Nested
    @DisplayName("DELETE /produto/deletar/{id}")
    class ExcluirEndpointTests {

        @Test
        @DisplayName("Deve excluir (marcar como deletado) o produto com sucesso")
        void excluirProdutoComSucesso() throws Exception {
            Produto produtoDeletado = new Produto(1L, "Descricao", 10, 100.0, true);

            when(produtoService.excluir(1L))
                    .thenReturn(produtoDeletado);

            mockMvc.perform(delete("/produto/deletar/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.deletado").value(true));
        }

        @Test
        @DisplayName("Deve tratar exceção e retornar BAD_REQUEST")
        void excluirProdutoComException() throws Exception {
            when(produtoService.excluir(1L))
                    .thenThrow(new NullPointerException("ID não pode ser nulo"));

            mockMvc.perform(delete("/produto/deletar/{id}", 1L))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("ID não pode ser nulo"));
        }
    }

    @Nested
    @DisplayName("GET /produto/{id}")
    class EncontrarEndpointTests {

        @Test
        @DisplayName("Deve encontrar o produto com sucesso")
        void encontrarProdutoComSucesso() throws Exception {
            when(produtoService.encontrarPeloId(1L))
                    .thenReturn(produto);

            mockMvc.perform(get("/produto/{id}", 1L))
                    .andExpect(status().isFound())
                    .andExpect(jsonPath("$.id").value(produto.getId()))
                    .andExpect(jsonPath("$.descricao").value(produto.getDescricao()));
        }

        @Test
        @DisplayName("Deve tratar RegistroNotFoundException e retornar NOT_FOUND")
        void encontrarProdutoNotFound() throws Exception {
            when(produtoService.encontrarPeloId(1L))
                    .thenThrow(new RegistroNotFoundException("Produto"));

            mockMvc.perform(get("/produto/{id}", 1L))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Produto não encontrado com este ID!"));
        }
    }

    @Nested
    @DisplayName("GET /produto/todos")
    class EncontrarTodosEndpointTests {

        @Test
        @DisplayName("Deve retornar todos os produtos")
        void encontrarTodosProdutos() throws Exception {
            List<Produto> produtos = List.of(
                    produto,
                    new Produto(2L, "Produto2", 5, 50.0, false)
            );

            when(produtoService.findAll()).thenReturn(produtos);

            mockMvc.perform(get("/produto/todos"))
                    .andExpect(status().isFound())
                    .andExpect(jsonPath("$", hasSize(produtos.size())))
                    .andExpect(jsonPath("$[0].id").value(produto.getId()));
        }
    }

    @Nested
    @DisplayName("GET /produto/todosValidos")
    class EncontrarTodosValidosEndpointTests {

        @Test
        @DisplayName("Deve retornar todos os produtos válidos")
        void encontrarTodosProdutosValidos() throws Exception {
            List<Produto> produtos = List.of(
                    produto,
                    new Produto(2L, "Produto2", 5, 50.0, false)
            );

            when(produtoService.findAllValidos()).thenReturn(produtos);

            mockMvc.perform(get("/produto/todosValidos"))
                    .andExpect(status().isFound())
                    .andExpect(jsonPath("$", hasSize(produtos.size())))
                    .andExpect(jsonPath("$[0].id").value(produto.getId()));
        }
    }

    @Nested
    @DisplayName("GET /produto/validarEstoque/{id}")
    class ValidarEstoqueEndpointTests {

        @Test
        @DisplayName("Deve validar o estoque com sucesso")
        void validarEstoqueComSucesso() throws Exception {
            when(produtoService.validarEstoque(1L, 5))
                    .thenReturn(produto);

            mockMvc.perform(get("/produto/validarEstoque/{id}", 1L)
                            .param("qtd", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(produto.getId()));
        }

        @Test
        @DisplayName("Deve tratar IllegalArgumentException e retornar BAD_REQUEST")
        void validarEstoqueComEstoqueInsuficiente() throws Exception {
            when(produtoService.validarEstoque(1L, 5))
                    .thenThrow(new IllegalArgumentException("Produto [Descricao] sem estoque suficiente!"));

            mockMvc.perform(get("/produto/validarEstoque/{id}", 1L)
                            .param("qtd", "5"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Produto [Descricao] sem estoque suficiente!"));
        }
    }

    @Nested
    @DisplayName("POST /produto/validarReduzirEstoque/{id}")
    class ValidarReduzirEstoqueEndpointTests {

        @Test
        @DisplayName("Deve validar e reduzir o estoque com sucesso")
        void validarReduzirEstoqueComSucesso() throws Exception {
            when(produtoService.validarReduzirEstoque(1L, 5))
                    .thenReturn(produto);

            mockMvc.perform(post("/produto/validarReduzirEstoque/{id}", 1L)
                            .param("qtd", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(produto.getId()));
        }

        @Test
        @DisplayName("Deve tratar exceção e retornar BAD_REQUEST")
        void validarReduzirEstoqueComException() throws Exception {
            when(produtoService.validarReduzirEstoque(1L, 5))
                    .thenThrow(new IllegalArgumentException("Produto [Descricao] sem estoque suficiente!"));

            mockMvc.perform(post("/produto/validarReduzirEstoque/{id}", 1L)
                            .param("qtd", "5"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Produto [Descricao] sem estoque suficiente!"));
        }
    }*/
}
