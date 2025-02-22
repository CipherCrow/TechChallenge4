package br.com.techchallenge.safedeliver.gerenciamentopedidos.controller;

import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.Cliente;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.Endereco;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.ItemPedido;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.Pedido;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.enums.StatusPedidoEnum;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.exception.ComunicacaoException;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.exception.RegistroNotFoundException;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.service.PedidoItemService;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.service.PedidoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PedidoControllerTest {

    private PedidoController pedidoController;

    @Mock
    private PedidoService pedidoService;

    @Mock
    private PedidoItemService pedidoItemService;

    private MockMvc mockMvc;
    private AutoCloseable openMocks;

    // Objetos dummy
    private Cliente cliente;
    private Endereco endereco;
    private ItemPedido itemPedido;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);

        pedidoController = new PedidoController(pedidoService,pedidoItemService);
        mockMvc = MockMvcBuilders.standaloneSetup(pedidoController)
                .setControllerAdvice(new br.com.techchallenge.safedeliver.gerenciamentopedidos.controller.GlobalExceptionHandler())
                .build();

        cliente = Cliente.builder()
                .id(1L)
                .nome("Cliente Teste")
                .cpf("12345678900")
                .email("cliente@teste.com")
                .telefone("123456789")
                .idade(30)
                .deletado(false)
                .build();

        endereco = Endereco.builder()
                .id(2L)
                .cep("12345-678")
                .cidade("Cidade Teste")
                .descricao("Endereco Teste")
                .numero(100)
                .cliente(cliente)
                .deletado(false)
                .build();

        itemPedido = ItemPedido.builder()
                .id(500L)
                .produto(null) // O produto é definido internamente
                .quantidade(5)
                .valorVendidoUnitario(10.0)
                .valorTotalItem(50.0)
                .build();

        pedido = Pedido.builder()
                .id(10L)
                .cliente(cliente)
                .endereco(null)
                .statusPedido(StatusPedidoEnum.EM_ANDAMENTO)
                .valorTotal(0.0)
                .itens(new ArrayList<>())
                .build();

    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    @DisplayName("POST /pedido/criar")
    class CriarPedido {
        @Test
        void criarPedidoSucesso() throws Exception {
            when(pedidoService.criar(1L)).thenReturn(pedido);

            mockMvc.perform(post("/pedido/criar")
                            .param("cliente", "1"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(pedido.getId()))
                    .andExpect(jsonPath("$.status").value(pedido.getStatusPedido().toString()));
            verify(pedidoService, times(1)).criar(1L);
        }
    }

    @Nested
    class InserirItem {
        @Test
        @DisplayName("Deve inserir item e atualizar valor total")
        void inserirItemSucesso() throws Exception {
            when(pedidoService.inserirItem(10L, 100L, 5))
                    .thenReturn(pedido);
            pedido.setValorTotal(10.0);
            when(pedidoService.atualizarValorTotal(10L)).thenReturn(pedido);

            mockMvc.perform(put("/pedido/inserirItem/{id}", 10L)
                            .param("idProduto", "100")
                            .param("quantidade", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.valorTotal").value(10.0));
            verify(pedidoService, times(1)).inserirItem(10L, 100L, 5);
            verify(pedidoService, times(1)).atualizarValorTotal(10L);
        }

        @Test
        @DisplayName("Deve retornar NOT_FOUND se não encontrar pedido")
            void inserirItemNotFound() throws Exception {
            doThrow(new RegistroNotFoundException("Pedido")).when(pedidoService).inserirItem(10L, 100L, 5);

            mockMvc.perform(put("/pedido/inserirItem/{id}", 10L)
                            .param("idProduto", "100")
                            .param("quantidade", "5"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Pedido não encontrado com este ID!"));
            verify(pedidoService, times(1)).inserirItem(10L, 100L, 5);
        }
    }

    @Nested
    @DisplayName("DELETE /pedido/removerItem/{id}")
    class RemoverItemEndpoint {
        @Test
        @DisplayName("Deve remover item e atualizar valor total")
        void removerItemSucesso() throws Exception {
            pedido.getItens().add(itemPedido);
            when(pedidoService.removerItem(10L, 500L)).thenReturn(pedido);
            when(pedidoService.atualizarValorTotal(10L)).thenReturn(pedido);

            mockMvc.perform(delete("/pedido/removerItem/{id}", 10L)
                            .param("codigoItem", "500"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.valorTotal").value(pedido.getValorTotal()));
            verify(pedidoService, times(1)).removerItem(10L, 500L);
            verify(pedidoService, times(1)).atualizarValorTotal(10L);
        }

        @Test
        @DisplayName("Deve retornar NOT_FOUND se item não existir")
        void removerItemErro() throws Exception {
            when(pedidoService.removerItem(10L, 999L))
                    .thenThrow(new RegistroNotFoundException("Item"));

            mockMvc.perform(delete("/pedido/removerItem/{id}", 10L)
                            .param("codigoItem", "999"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Item não encontrado com este ID!"));
            verify(pedidoService, times(1)).removerItem(10L, 999L);
        }
    }

    @Nested
    @DisplayName("GET /pedido/{id}")
    class EncontrarPedidoEndpoint {
        @Test
        @DisplayName("Sucesso: Deve retornar pedido por ID")
        void encontrarPedidoSucesso() throws Exception {
            when(pedidoService.encontrarPeloId(10L)).thenReturn(pedido);

            mockMvc.perform(get("/pedido/{id}", 10L))
                    .andExpect(status().isFound())
                    .andExpect(jsonPath("$.id").value(pedido.getId()));
            verify(pedidoService, times(1)).encontrarPeloId(10L);
        }

        @Test
        @DisplayName("Deve retornar NOT_FOUND se pedido não for encontrado")
        void encontrarPedidoErro() throws Exception {
            when(pedidoService.encontrarPeloId(10L))
                    .thenThrow(new RegistroNotFoundException("Pedido"));

            mockMvc.perform(get("/pedido/{id}", 10L))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Pedido não encontrado com este ID!"));
            verify(pedidoService, times(1)).encontrarPeloId(10L);
        }
    }

    @Nested
    @DisplayName("GET /pedido/todos")
    class ListarPedidosEndpoint {
        @Test
        @DisplayName("Deve retornar lista de pedidos")
        void listarPedidosSucesso() throws Exception {
            when(pedidoService.listarTodos()).thenReturn(List.of(pedido));

            mockMvc.perform(get("/pedido/todos"))
                    .andExpect(status().isFound())
                    .andExpect(jsonPath("$[0].id").value(pedido.getId()));
            verify(pedidoService, times(1)).listarTodos();
        }
    }

    @Nested
    @DisplayName("PUT /pedido/confirmarPedido/{id}")
    class ConfirmarPedidoEndpoint {
        @Test
        @DisplayName("Deve confirmar pedido com sucesso")
        void confirmarPedidoSucesso() throws Exception {
            pedido.setStatusPedido(StatusPedidoEnum.EM_ANDAMENTO);
            pedido.setEndereco(null);
            // Simula confirmação: após confirmação, o pedido tem status CONFIRMADO e endereço preenchido
            pedido.setStatusPedido(StatusPedidoEnum.CONFIRMADO);
            pedido.setEndereco(endereco);
            when(pedidoService.confirmarPedido(10L, 2L)).thenReturn(pedido);

            mockMvc.perform(put("/pedido/confirmarPedido/{id}", 10L)
                            .param("endereco", "2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("CONFIRMADO"))
                    .andExpect(jsonPath("$.endereco.id").value(endereco.getId()));
            verify(pedidoService, times(1)).confirmarPedido(10L, 2L);
        }

        @Test
        @DisplayName("Deve retornar BAD_REQUEST se o pedido não estiver em andamento")
        void confirmarPedidoNaoAndamento() throws Exception {
            when(pedidoService.confirmarPedido(10L, 2L))
                    .thenThrow(new IllegalArgumentException("Apenas pedidos em andamento podem ser confirmados!"));

            mockMvc.perform(put("/pedido/confirmarPedido/{id}", 10L)
                            .param("endereco", "2"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Apenas pedidos em andamento podem ser confirmados!"));
            verify(pedidoService, times(1)).confirmarPedido(10L, 2L);
        }

        @Test
        @DisplayName("Deve retornar BAD_REQUEST se nao achar o produto")
        void confirmarPedidoErroRegistroNotFound() throws Exception {
            when(pedidoService.confirmarPedido(10L, 2L))
                    .thenThrow(new RegistroNotFoundException("Produto"));

            mockMvc.perform(put("/pedido/confirmarPedido/{id}", 10L)
                            .param("endereco", "2"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Produto não encontrado com este ID!"));
            verify(pedidoService, times(1)).confirmarPedido(10L, 2L);
        }

        @Test
        @DisplayName("Deve retornar BAD_REQUEST se houver problema de comunicacao")
        void confirmarPedidoErroComunicacao() throws Exception {
            when(pedidoService.confirmarPedido(10L, 2L))
                    .thenThrow(new ComunicacaoException("Produto"));

            mockMvc.perform(put("/pedido/confirmarPedido/{id}", 10L)
                            .param("endereco", "2"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Erro ao comunicar com o serviço de Produto"));
            verify(pedidoService, times(1)).confirmarPedido(10L, 2L);
        }
    }

    @Nested
    @DisplayName("DELETE /pedido/deletarPedido/{id}")
    class CancelarPedidoEndpoint {
        @Test
        @DisplayName("Sucesso: Deve cancelar pedido com sucesso")
        void cancelarPedidoSucesso() throws Exception {
            pedido.setStatusPedido(StatusPedidoEnum.EM_ANDAMENTO);
            when(pedidoService.cancelarPedido(10L)).thenReturn(pedido);
            pedido.setStatusPedido(StatusPedidoEnum.CANCELADO);
            when(pedidoService.cancelarPedido(10L)).thenReturn(pedido);

            mockMvc.perform(delete("/pedido/deletarPedido/{id}", 10L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("CANCELADO"));
            verify(pedidoService, times(1)).cancelarPedido(10L);
        }

        @Test
        @DisplayName("Deve retornar BAD_REQUEST se tentar cancelar pedido que nn está em andamento")
        void cancelarPedidoNaoEmAndamento() throws Exception {
            when(pedidoService.cancelarPedido(10L)).
                    thenThrow(new IllegalArgumentException("Apenas pedidos em andamento podem ser cancelados!"));

            mockMvc.perform(delete("/pedido/deletarPedido/{id}", 10L))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Apenas pedidos em andamento podem ser cancelados!"));
            verify(pedidoService, times(1)).cancelarPedido(10L);
        }

        @Test
        @DisplayName("Deve retornar NOT_FOUND se tentar cancelar um pedido e nn achar")
        void cancelarPedidoPedidoNaoExiste() throws Exception {
            when(pedidoService.cancelarPedido(10L)).
                    thenThrow(new RegistroNotFoundException("Pedido"));

            mockMvc.perform(delete("/pedido/deletarPedido/{id}", 10L))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Pedido não encontrado com este ID!"));
            verify(pedidoService, times(1)).cancelarPedido(10L);
        }
    }
}
