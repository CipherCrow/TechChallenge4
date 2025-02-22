package br.com.techchallenge.safedeliver.gerenciamentoclientes.service;

import br.com.techchallenge.safedeliver.gerenciamentoclientes.domain.model.entities.Cliente;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.exception.RegistroNotFoundException;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.repository.ClienteRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ClienteServiceTest {

    private ClienteServiceImpl clienteService;

    @Mock
    private ClienteRepository clienteRepository;

    private AutoCloseable openMocks;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        clienteRepository = mock(ClienteRepository.class);
        clienteService = new ClienteServiceImpl(clienteRepository);

        cliente = new Cliente(1L, "Nome", "12345678901", "email@example.com", "123456789", 30, false);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    @DisplayName("criar")
    class CriarTests {

        @Test
        @DisplayName("Deve criar um cliente com sucesso")
        void criarClienteComSucesso() {
            when(clienteRepository.save(cliente)).thenReturn(cliente);

            Cliente result = clienteService.criar(cliente);

            verify(clienteRepository, times(1)).save(cliente);
            assertThat(result).isEqualTo(cliente);
        }
    }

    @Nested
    @DisplayName("atualizar")
    class AtualizarTests {

        @Test
        @DisplayName("Deve atualizar um cliente com sucesso")
        void atualizarClienteComSucesso() {
            Cliente novoCliente = new Cliente(null, "Novo Nome", "11122233344", "novoemail@example.com", "987654321", 35, false);

            when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
            when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Cliente result = clienteService.atualizar(novoCliente, cliente.getId());

            assertThat(result.getNome()).isEqualTo("Novo Nome");
            assertThat(result.getCpf()).isEqualTo("11122233344");
            assertThat(result.getEmail()).isEqualTo("novoemail@example.com");
            assertThat(result.getTelefone()).isEqualTo("987654321");
            assertThat(result.getIdade()).isEqualTo(35);
            verify(clienteRepository, times(1)).findById(cliente.getId());
            verify(clienteRepository, times(1)).save(any(Cliente.class));
        }

        @Test
        @DisplayName("Deve lançar Exception se o ID for nulo")
        void atualizarClienteComIdNulo() {
            Cliente novoCliente = new Cliente(null, "Novo Nome", "11122233344", "novoemail@example.com", "987654321", 35, false);

            assertThatThrownBy(() -> clienteService.atualizar(novoCliente, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("ID não pode ser nulo");
        }

        @Test
        @DisplayName("Deve lançar RegistroNotFoundException se o cliente não for encontrado")
        void atualizarClienteNaoExistente() {
            Cliente novoCliente = new Cliente(null, "Novo Nome", "11122233344", "novoemail@example.com", "987654321", 35, false);

            when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> clienteService.atualizar(novoCliente, cliente.getId()))
                    .isInstanceOf(RegistroNotFoundException.class)
                    .hasMessageContaining("Cliente não encontrado com este ID!");
        }
    }

    @Nested
    @DisplayName("excluir")
    class ExcluirTests {

        @Test
        @DisplayName("Deve marcar o cliente como deletado com sucesso")
        void excluirClienteComSucesso() {
            when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
            when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Cliente result = clienteService.excluir(cliente.getId());

            assertThat(result.isDeletado()).isTrue();
            verify(clienteRepository, times(1)).findById(cliente.getId());
            verify(clienteRepository, times(1)).save(any(Cliente.class));
        }

        @Test
        @DisplayName("Deve lançar Exception se o ID for nulo")
        void excluirClienteComIdNulo() {
            assertThatThrownBy(() -> clienteService.excluir(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("ID não pode ser nulo");
        }

        @Test
        @DisplayName("Deve lançar RegistroNotFoundException se o cliente não for encontrado")
        void excluirClienteNaoExistente() {
            when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> clienteService.excluir(cliente.getId()))
                    .isInstanceOf(RegistroNotFoundException.class)
                    .hasMessageContaining("Cliente não encontrado com este ID!");
        }
    }

    @Nested
    @DisplayName("Testes para o método listarTodos")
    class ListarTodosTests {

        @Test
        @DisplayName("Deve retornar a lista de todos os clientes")
        void listarTodosClientes() {
            Cliente outroCliente = new Cliente(2L, "Outro Nome", "22233344455", "outroemail@example.com", "111222333", 40, false);
            List<Cliente> lista = List.of(cliente, outroCliente);

            when(clienteRepository.findAll()).thenReturn(lista);

            List<Cliente> result = clienteService.listarTodos();

            assertThat(result).hasSize(2).containsAll(lista);
            verify(clienteRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("encontrarPeloId")
    class FindByIdTests {

        @Test
        @DisplayName("Deve retornar o cliente se encontrado")
        void findByIdComSucesso() {
            when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));

            Cliente result = clienteService.encontrarPeloID(cliente.getId());

            assertThat(result).isEqualTo(cliente);
            verify(clienteRepository, times(1)).findById(cliente.getId());
        }

        @Test
        @DisplayName("Deve lançar Exception se o ID for nulo")
        void findByIdComIdNulo() {
            assertThatThrownBy(() -> clienteService.encontrarPeloID(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("ID não pode ser nulo");
        }

        @Test
        @DisplayName("Deve lançar RegistroNotFoundException se o cliente não for encontrado")
        void findByIdNaoEncontrado() {
            when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> clienteService.encontrarPeloID(cliente.getId()))
                    .isInstanceOf(RegistroNotFoundException.class)
                    .hasMessageContaining("Cliente não encontrado com este ID!");
        }
    }
}