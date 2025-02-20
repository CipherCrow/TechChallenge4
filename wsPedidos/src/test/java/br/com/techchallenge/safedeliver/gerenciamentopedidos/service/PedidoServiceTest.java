package br.com.techchallenge.safedeliver.gerenciamentopedidos.service;

import br.com.techchallenge.safedeliver.gerenciamentopedidos.client.ClienteClient;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.client.EnderecoClient;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.client.ProdutoClient;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.*;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.enums.StatusPedidoEnum;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.exception.ComunicacaoException;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.exception.RegistroNotFoundException;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.mapper.ClienteMapper;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.mapper.EnderecoMapper;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.mapper.ProdutoMapper;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.repository.PedidoRepository;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class PedidoServiceTest {

    private PedidoServiceImpl pedidoService;

    @Mock
    private ProdutoClient produtoClient;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PedidoItemService pedidoItemService;

    @Mock
    private ClienteClient clienteClient;

    @Mock
    private EnderecoClient enderecoClient;

    private AutoCloseable openMocks;

    private Cliente cliente;
    private Endereco endereco;
    private Produto produto;
    private ItemPedido itemPedido;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        pedidoService = new PedidoServiceImpl(produtoClient, pedidoRepository, pedidoItemService, clienteClient, enderecoClient);

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

        produto = br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.Produto.builder()
                .id(100L)
                .descricao("Produto Teste")
                .estoque(50)
                .preco(10.0)
                .deletado(false)
                .build();

        itemPedido = ItemPedido.builder()
                .id(500L)
                .produto(produto)
                .quantidade(5)
                .valorVendidoUnitario(produto.getPreco())
                .valorTotalItem(5 * produto.getPreco())
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
    @DisplayName("criar")
    class CriarTests {
        @Test
        @DisplayName("Deve criar um pedido com sucesso")
        void criarPedidoSucesso() {
            // Simula o retorno do clienteClient
            when(clienteClient.encontrarCliente(1L)).thenReturn(
                    ClienteMapper.toDTO(cliente)
            );

            when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
                Pedido p = invocation.getArgument(0);
                p.setId(10L);
                return p;
            });

            Pedido result = pedidoService.criar(1L);

            assertThat(result.getId()).isEqualTo(10L);
            assertThat(result.getCliente()).usingRecursiveComparison().isEqualTo(cliente);
            assertThat(result.getStatusPedido()).isEqualTo(StatusPedidoEnum.EM_ANDAMENTO);
            verify(clienteClient, times(1)).encontrarCliente(1L);
            verify(pedidoRepository, times(1)).save(any(Pedido.class));
        }

        @Test
        @DisplayName("Deve lançar NullPointerException se o clienteInformado for nulo")
        void criarPedidoClienteNulo() {
            assertThatThrownBy(() -> pedidoService.criar(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("ID não pode ser nulo");
            verifyNoInteractions(clienteClient);
            verifyNoInteractions(pedidoRepository);
        }

        @Test
        void criarPedidoCLienteNotFound() {
            Request request = Request.create(Request.HttpMethod.GET, "/dummy",
                    Collections.emptyMap(), null, Charset.defaultCharset(), null);
            FeignException.NotFound notFoundException = new FeignException.NotFound("Not Found", request, null,null);

            when(clienteClient.encontrarCliente(1L)).thenThrow(notFoundException);

            assertThatThrownBy(() -> pedidoService.criar(1L))
                    .isInstanceOf(RegistroNotFoundException.class)
                    .hasMessageContaining("Cliente não encontrado com este ID!");
            verify(clienteClient, times(1)).encontrarCliente(1L);
        }

        @Test
        void criarPedidoFalhaNaComunicacao() {
            Request request = Request.create(Request.HttpMethod.GET, "/dummy",
                    Collections.emptyMap(), null, Charset.defaultCharset(), null);
            FeignException genericException = new FeignException.InternalServerError("Error", request, null,null);

            when(clienteClient.encontrarCliente(1L)).thenThrow(genericException);

            assertThatThrownBy(() -> pedidoService.criar(1L))
                    .isInstanceOf(ComunicacaoException.class)
                    .hasMessageContaining("Erro ao comunicar com o serviço de Cliente");
            verify(clienteClient, times(1)).encontrarCliente(1L);
        }
    }

    @Nested
    @DisplayName("inserirItem")
    class InserirItemTests {
        @Test
        @DisplayName("Deve inserir item no pedido com sucesso")
        void inserirItemSucesso() {
            pedido.getItens().clear();
            when(pedidoRepository.findById(10L)).thenReturn(Optional.of(pedido));
            when(pedidoItemService.criar(10L, 100L, 5)).thenReturn(itemPedido);
            when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Pedido result = pedidoService.inserirItem(10L, 100L, 5);

            assertThat(result.getItens()).hasSize(1);
            assertThat(result.getItens().get(0)).usingRecursiveComparison().isEqualTo(itemPedido);
            verify(pedidoRepository, times(1)).findById(10L);
            verify(pedidoItemService, times(1)).criar(10L, 100L, 5);
            verify(pedidoRepository, times(1)).save(any(Pedido.class));
        }

        @Test
        @DisplayName("Deve dar exception caso nn encontre o pedido")
        void inserirItemNaoEncontrado() {
            when(pedidoRepository.findById(10L)).thenThrow(RegistroNotFoundException.class);

            assertThatThrownBy(() -> pedidoService.inserirItem(13L,100L, 5))
                    .isInstanceOf(RegistroNotFoundException.class)
                    .hasMessageContaining("Pedido não encontrado com este ID!");
        }

        @Test
        @DisplayName("Deve lançar NullPointerException se codigoPedido ou codigoItem forem nulos")
        void inserirItemNulos() {
            assertThatThrownBy(() -> pedidoService.inserirItem(null, 100L, 5))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("ID não pode ser nulo");
            assertThatThrownBy(() -> pedidoService.inserirItem(10L, null, 5))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("ID não pode ser nulo");
            verifyNoInteractions(pedidoRepository);
            verifyNoInteractions(pedidoItemService);
        }
    }

    @Nested
    @DisplayName("removerItem")
    class RemoverItemTests {
        @Test
        @DisplayName("Deve remover item do pedido com sucesso")
        void removerItemSucesso() {
            pedido.getItens().clear();
            pedido.getItens().add(itemPedido);
            when(pedidoRepository.findById(10L)).thenReturn(Optional.of(pedido));
            when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Pedido result = pedidoService.removerItem(10L, itemPedido.getId());

            assertThat(result.getItens()).isEmpty();
            verify(pedidoRepository, times(1)).findById(10L);
            verify(pedidoRepository, times(1)).save(pedido);
        }

        @Test
        @DisplayName("Deve lançar RegistroNotFoundException se item não existir no pedido")
        void removerItemNaoEncontrado() {
            pedido.getItens().clear();
            when(pedidoRepository.findById(10L)).thenReturn(Optional.of(pedido));

            assertThatThrownBy(() -> pedidoService.removerItem(10L, 999L))
                    .isInstanceOf(RegistroNotFoundException.class)
                    .hasMessageContaining("Item não encontrado com este ID!");
            verify(pedidoRepository, times(1)).findById(10L);
            verify(pedidoRepository, never()).save(any(Pedido.class));
        }

        @Test
        @DisplayName("Deve lançar NullPointerException se codigoPedido ou codigoItemPedido forem nulos")
        void removerItemNulos() {
            assertThatThrownBy(() -> pedidoService.removerItem(null, 500L))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("ID não pode ser nulo");
            assertThatThrownBy(() -> pedidoService.removerItem(10L, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("ID não pode ser nulo");
            verifyNoInteractions(pedidoRepository);
        }
    }

    @Nested
    @DisplayName("encontrarPeloId")
    class EncontrarPeloIdTests {
        @Test
        @DisplayName("Deve retornar pedido se encontrado")
        void encontrarPeloIdSucesso() {
            when(pedidoRepository.findById(10L)).thenReturn(Optional.of(pedido));
            Pedido result = pedidoService.encontrarPeloId(10L);
            assertThat(result).usingRecursiveComparison().isEqualTo(pedido);
            verify(pedidoRepository, times(1)).findById(10L);
        }

        @Test
        @DisplayName("Deve lançar NullPointerException se codigoPedido for nulo")
        void encontrarPeloIdNulo() {
            assertThatThrownBy(() -> pedidoService.encontrarPeloId(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("ID não pode ser nulo");
            verifyNoInteractions(pedidoRepository);
        }

        @Test
        @DisplayName("Deve lançar RegistroNotFoundException se pedido não for encontrado")
        void encontrarPeloIdNaoEncontrado() {
            when(pedidoRepository.findById(10L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> pedidoService.encontrarPeloId(10L))
                    .isInstanceOf(RegistroNotFoundException.class)
                    .hasMessageContaining("Pedido não encontrado com este ID!");
            verify(pedidoRepository, times(1)).findById(10L);
        }
    }

    @Nested
    @DisplayName("listarTodos")
    class ListarTodosTests {
        @Test
        @DisplayName("Deve retornar lista de pedidos")
        void listarTodosSucesso() {
            when(pedidoRepository.findAll()).thenReturn(List.of(pedido));
            List<Pedido> result = pedidoService.listarTodos();
            assertThat(result).containsExactly(pedido);
            verify(pedidoRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("confirmarPedido")
    class ConfirmarPedidoTests {
        @Test
        @DisplayName("Deve confirmar pedido com sucesso")
        void confirmarPedidoSucesso() {
            pedido.setStatusPedido(StatusPedidoEnum.EM_ANDAMENTO);
            pedido.setEndereco(null);

            when(pedidoRepository.findById(10L)).thenReturn(Optional.of(pedido));

            // Cria um spy do serviço para stubar o método validarProdutosParaFechamento
            PedidoServiceImpl spyPedidoService = spy(pedidoService);
            doReturn(true).when(spyPedidoService).validarProdutosParaFechamento(pedido.getItens());

            // Stub para o enderecoClient: converter o dummyEndereco para DTO
            when(enderecoClient.encontrarEndereco(2L)).thenReturn(EnderecoMapper.toDTO(endereco));
            // Stub para salvar o pedido
            when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Chama o método confirmarPedido usando o spy
            Pedido result = spyPedidoService.confirmarPedido(10L, 2L);

            assertThat(result.getStatusPedido()).isEqualTo(StatusPedidoEnum.CONFIRMADO);
            assertThat(result.getEndereco()).usingRecursiveComparison().isEqualTo(endereco);
            verify(pedidoRepository, times(1)).findById(10L);
            verify(pedidoRepository, times(1)).save(pedido);
        }


        @Test
        @DisplayName("Deve lançar IllegalArgumentException se o pedido não estiver em andamento para confirmação")
        void confirmarPedidoStatusInvalido() {
            pedido.setStatusPedido(StatusPedidoEnum.CONFIRMADO);
            when(pedidoRepository.findById(10L)).thenReturn(Optional.of(pedido));

            assertThatThrownBy(() -> pedidoService.confirmarPedido(10L, 2L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Apenas pedidos em andamento podem ser confirmados!");
            verify(pedidoRepository, times(1)).findById(10L);
            verify(pedidoRepository, never()).save(any(Pedido.class));
        }
    }

    @Nested
    @DisplayName("cancelarPedido")
    class CancelarPedidoTests {
        @Test
        @DisplayName("Deve cancelar pedido com sucesso")
        void cancelarPedidoSucesso() {
            pedido.setStatusPedido(StatusPedidoEnum.EM_ANDAMENTO);
            when(pedidoRepository.findById(10L)).thenReturn(Optional.of(pedido));
            when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Pedido result = pedidoService.cancelarPedido(10L);

            assertThat(result.getStatusPedido()).isEqualTo(StatusPedidoEnum.CANCELADO);
            verify(pedidoRepository, times(1)).findById(10L);
            verify(pedidoRepository, times(1)).save(pedido);
        }

        @Test
        @DisplayName("Deve lançar IllegalArgumentException se o pedido não estiver em andamento para cancelamento")
        void cancelarPedidoStatusInvalido() {
            pedido.setStatusPedido(StatusPedidoEnum.CONFIRMADO);
            when(pedidoRepository.findById(10L)).thenReturn(Optional.of(pedido));

            assertThatThrownBy(() -> pedidoService.cancelarPedido(10L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Apenas pedidos em andamento podem ser cancelados!");
            verify(pedidoRepository, times(1)).findById(10L);
            verify(pedidoRepository, never()).save(any(Pedido.class));
        }
    }

    @Nested
    @DisplayName("Testes para o método atualizarValorTotal")
    class AtualizarValorTotalTests {
        @Test
        @DisplayName("Deve atualizar o valor total do pedido corretamente")
        void atualizarValorTotalSucesso() {
            pedido.getItens().clear();
            pedido.getItens().add(itemPedido); // Valor unitário = 10.0
            when(pedidoRepository.findById(10L)).thenReturn(Optional.of(pedido));
            when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Pedido result = pedidoService.atualizarValorTotal(10L);

            // Como há 1 item com valor 10.0, o total deve ser 10.0
            assertThat(result.getValorTotal()).isEqualTo(10.0);
            verify(pedidoRepository, times(1)).findById(10L);
            verify(pedidoRepository, times(1)).save(pedido);
        }
    }

    @Nested
    @DisplayName("encontrarCliente")
    class EncontrarClienteTests {
        @Test
        @DisplayName("Deve retornar cliente se encontrado")
        void encontrarClienteSucesso() {
            when(clienteClient.encontrarCliente(1L)).thenReturn(
                    new br.com.techchallenge.safedeliver.gerenciamentopedidos.dto.ClienteDTO(
                            cliente.getId(), cliente.getNome(), cliente.getCpf(),
                            cliente.getEmail(), cliente.getTelefone(), cliente.getIdade(), cliente.isDeletado()
                    )
            );
            Cliente result = pedidoService.encontrarCliente(1L);
            assertThat(result).usingRecursiveComparison().isEqualTo(cliente);
            verify(clienteClient, times(1)).encontrarCliente(1L);
        }

        @Test
        @DisplayName("Deve lançar RegistroNotFoundException se cliente não for encontrado")
        void encontrarClienteNotFound() {
            Request request = Request.create(Request.HttpMethod.GET, "/dummy", Collections.emptyMap(), null, Charset.defaultCharset(), null);
            FeignException.NotFound notFound = new FeignException.NotFound("Not Found", request, null,null);
            when(clienteClient.encontrarCliente(1L)).thenThrow(notFound);

            assertThatThrownBy(() -> pedidoService.encontrarCliente(1L))
                    .isInstanceOf(RegistroNotFoundException.class)
                    .hasMessageContaining("Cliente não encontrado com este ID!");
            verify(clienteClient, times(1)).encontrarCliente(1L);
        }

        @Test
        @DisplayName("Deve lançar ComunicacaoException se ocorrer erro no clienteClient")
        void encontrarClienteComunicacaoException() {
            Request request = Request.create(Request.HttpMethod.GET, "/dummy", Collections.emptyMap(), null, Charset.defaultCharset(), null);
            FeignException generic = new FeignException.InternalServerError("Error", request, null,null);
            when(clienteClient.encontrarCliente(1L)).thenThrow(generic);

            assertThatThrownBy(() -> pedidoService.encontrarCliente(1L))
                    .isInstanceOf(ComunicacaoException.class)
                    .hasMessage("Erro ao comunicar com o serviço de Cliente");
            verify(clienteClient, times(1)).encontrarCliente(1L);
        }
    }

    @Nested
    @DisplayName("encontrarEndereco")
    class EncontrarEnderecoTests {
        @Test
        @DisplayName("Deve retornar endereco se encontrado")
        void encontrarEnderecoSucesso() {
            when(enderecoClient.encontrarEndereco(2L)).thenReturn(
                    new br.com.techchallenge.safedeliver.gerenciamentopedidos.dto.EnderecoDTO(
                            endereco.getId(), endereco.getCep(), endereco.getCidade(),
                            endereco.getDescricao(), endereco.getNumero(), endereco.getCliente(), endereco.isDeletado()
                    )
            );
            Endereco result = pedidoService.encontrarEndereco(2L);
            assertThat(result).usingRecursiveComparison().isEqualTo(endereco);
            verify(enderecoClient, times(1)).encontrarEndereco(2L);
        }

        @Test
        @DisplayName("Deve lançar RegistroNotFoundException se endereco não for encontrado")
        void encontrarEnderecoNotFound() {
            Request request = Request.create(Request.HttpMethod.GET, "/dummy", Collections.emptyMap(), null, Charset.defaultCharset(), null);
            FeignException.NotFound notFound = new FeignException.NotFound("Not Found", request, null,null);
            when(enderecoClient.encontrarEndereco(2L)).thenThrow(notFound);

            assertThatThrownBy(() -> pedidoService.encontrarEndereco(2L))
                    .isInstanceOf(RegistroNotFoundException.class)
                    .hasMessageContaining("Endereco não encontrado com este ID!");
            verify(enderecoClient, times(1)).encontrarEndereco(2L);
        }

        @Test
        @DisplayName("Deve lançar ComunicacaoException se ocorrer erro no enderecoClient")
        void encontrarEnderecoComunicacaoException() {
            Request request = Request.create(Request.HttpMethod.GET, "/dummy", Collections.emptyMap(), null, Charset.defaultCharset(), null);
            FeignException generic = new FeignException.InternalServerError("Error", request, null,null);
            when(enderecoClient.encontrarEndereco(2L)).thenThrow(generic);

            assertThatThrownBy(() -> pedidoService.encontrarEndereco(2L))
                    .isInstanceOf(ComunicacaoException.class)
                    .hasMessage("Erro ao comunicar com o serviço de Endereco");
            verify(enderecoClient, times(1)).encontrarEndereco(2L);
        }
    }

    @Nested
    @DisplayName("validarProdutosParaFechamento")
    class ValidarProdutosParaFechamentoTests {
        @Test
        @DisplayName("Deve validar produtos para fechamento com sucesso")
        void validarProdutosSucesso() {
            List<ItemPedido> itens = List.of(itemPedido);
            when(produtoClient.validaReduzirEstoque(produto.getId(), itemPedido.getQuantidade()))
                    .thenReturn(ProdutoMapper.toDTO(produto));

            boolean result = pedidoService.validarProdutosParaFechamento(itens);
            assertThat(result).isTrue();
            verify(produtoClient, times(1)).validaReduzirEstoque(produto.getId(), itemPedido.getQuantidade());
        }

        @Test
        @DisplayName("Deve lançar RegistroNotFoundException se produto não for encontrado")
        void validarProdutosNotFound() {
            List<ItemPedido> itens = List.of(itemPedido);
            Request request = Request.create(Request.HttpMethod.GET, "/dummy", Collections.emptyMap(), null, Charset.defaultCharset(), null);
            FeignException.NotFound notFound = new FeignException.NotFound("Not Found", request, null,null);
            when(produtoClient.validaReduzirEstoque(produto.getId(), itemPedido.getQuantidade()))
                    .thenThrow(notFound);

            assertThatThrownBy(() -> pedidoService.validarProdutosParaFechamento(itens))
                    .isInstanceOf(RegistroNotFoundException.class)
                    .hasMessageContaining("Produto não encontrado com este ID!");
            verify(produtoClient, times(1)).validaReduzirEstoque(produto.getId(), itemPedido.getQuantidade());
        }

        @Test
        @DisplayName("Deve lançar IllegalArgumentException se produto retornar BadRequest")
        void validarProdutosBadRequest() {
            List<ItemPedido> itens = List.of(itemPedido);
            Request request = Request.create(Request.HttpMethod.GET, "/dummy", Collections.emptyMap(), null, Charset.defaultCharset(), null);
            FeignException.BadRequest badRequest = new FeignException.BadRequest("Bad Request", request, null,null);
            when(produtoClient.validaReduzirEstoque(produto.getId(), itemPedido.getQuantidade()))
                    .thenThrow(badRequest);

            assertThatThrownBy(() -> pedidoService.validarProdutosParaFechamento(itens))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Bad Request");
            verify(produtoClient, times(1)).validaReduzirEstoque(produto.getId(), itemPedido.getQuantidade());
        }

        @Test
        @DisplayName("Deve lançar ComunicacaoException se ocorrer outro erro no produtoClient")
        void validarProdutosComunicacaoException() {
            List<ItemPedido> itens = List.of(itemPedido);
            Request request = Request.create(Request.HttpMethod.GET, "/dummy", Collections.emptyMap(), null, Charset.defaultCharset(), null);
            FeignException generic = new FeignException.InternalServerError("Error", request, null,null);
            when(produtoClient.validaReduzirEstoque(produto.getId(), itemPedido.getQuantidade()))
                    .thenThrow(generic);

            assertThatThrownBy(() -> pedidoService.validarProdutosParaFechamento(itens))
                    .isInstanceOf(ComunicacaoException.class)
                    .hasMessage("Erro ao comunicar com o serviço de Produto");
            verify(produtoClient, times(1)).validaReduzirEstoque(produto.getId(), itemPedido.getQuantidade());
        }
    }
}
