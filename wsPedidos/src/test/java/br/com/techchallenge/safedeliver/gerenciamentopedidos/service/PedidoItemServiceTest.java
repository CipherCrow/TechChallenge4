package br.com.techchallenge.safedeliver.gerenciamentopedidos.service;

import br.com.techchallenge.safedeliver.gerenciamentopedidos.client.ProdutoClient;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.ItemPedido;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.Produto;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.dto.ProdutoDTO;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.exception.ComunicacaoException;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.exception.RegistroNotFoundException;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.mapper.ProdutoMapper;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.repository.ItemPedidoRepository;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.charset.Charset;
import java.util.Collections;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PedidoItemServiceTest {

    private PedidoItemServiceImpl pedidoItemService;

    @Mock
    private ItemPedidoRepository itemPedidoRepository;

    @Mock
    private ProdutoClient produtoClient;

    private AutoCloseable openMocks;

    private Produto produto;

    private ProdutoDTO produtoDTO;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);

        this.pedidoItemService = new PedidoItemServiceImpl(itemPedidoRepository, produtoClient);

        produto = Produto.builder()
                .id(100L)
                .descricao("Produto Teste")
                .estoque(50)
                .preco(10.0)
                .deletado(false)
                .build();

        produtoDTO = ProdutoMapper.toDTO(produto);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    class CriarTests {
        @Test
        void criarItemPedidoSucesso() {
            when(produtoClient.validaEstoque(100L, 5)).thenReturn(produtoDTO);

            // Chama o método criar
            ItemPedido item = pedidoItemService.criar(1L, 100L, 5);

            // Verifica se os campos foram corretamente configurados
            assertThat(item.getProduto()).isEqualTo(produto);
            assertThat(item.getQuantidade()).isEqualTo(5);
            assertThat(item.getValorVendidoUnitario()).isEqualTo(produto.getPreco());
            assertThat(item.getValorTotalItem()).isEqualTo(5 * produto.getPreco());

            verify(produtoClient, times(1)).validaEstoque(100L, 5);
        }
    }

    @Nested
    class EncontrarValidarProdutoTests {
        @Test
        void encontrarValidarProdutoSucesso() {
            // Simula retorno do produtoClient
            when(produtoClient.validaEstoque(100L, 5)).thenReturn(produtoDTO);

            Produto result = pedidoItemService.encontrarValidarProduto(100L, 5);

            // Utiliza comparação recursiva para verificar a equivalência
            assertThat(result).usingRecursiveComparison().isEqualTo(produto);
            verify(produtoClient, times(1)).validaEstoque(100L, 5);
        }

        @Test
        void encontrarValidarProdutoNotFound() {
            Request request = Request.create(Request.HttpMethod.GET, "/dummy",
                    Collections.emptyMap(), null, Charset.defaultCharset(), null);
            FeignException.NotFound notFoundException = new FeignException.NotFound("Not Found", request, null,null);

            when(produtoClient.validaEstoque(100L, 5)).thenThrow(notFoundException);

            assertThatThrownBy(() -> pedidoItemService.encontrarValidarProduto(100L, 5))
                    .isInstanceOf(RegistroNotFoundException.class)
                    .hasMessageContaining("Produto não encontrado com este ID!");
            verify(produtoClient, times(1)).validaEstoque(100L, 5);
        }

        @Test
        void encontrarValidarProdutoComunicacaoException() {
            Request request = Request.create(Request.HttpMethod.GET, "/dummy",
                    Collections.emptyMap(), null, Charset.defaultCharset(), null);
            FeignException genericException = new FeignException.InternalServerError("Error", request, null,null);

            when(produtoClient.validaEstoque(100L, 5)).thenThrow(genericException);

            assertThatThrownBy(() -> pedidoItemService.encontrarValidarProduto(100L, 5))
                    .isInstanceOf(ComunicacaoException.class)
                    .hasMessage("Erro ao comunicar com o serviço de Produto");
            verify(produtoClient, times(1)).validaEstoque(100L, 5);
        }
    }
}
