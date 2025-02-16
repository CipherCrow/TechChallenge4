package br.com.techchallenge.safedeliver.gerenciamentoclientes.service;

import br.com.techchallenge.safedeliver.gerenciamentoclientes.domain.model.entities.Cliente;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.domain.model.entities.Endereco;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.exception.RegistroNotFoundException;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.repository.EnderecoRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EnderecoServiceTest {

    private EnderecoServiceImpl enderecoService;

    @Mock
    private EnderecoRepository enderecoRepository;

    @Mock
    private ClienteService clienteService;

    private AutoCloseable openMocks;

    private Endereco endereco;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        enderecoRepository = mock(EnderecoRepository.class);
        clienteService = mock(ClienteService.class);
        enderecoService = new EnderecoServiceImpl(enderecoRepository, clienteService);

        // Configuração de objetos de exemplo
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Cliente Teste");
        cliente.setCpf("12345678901");
        cliente.setEmail("cliente@teste.com");
        cliente.setTelefone("99999999");
        cliente.setIdade(30);
        cliente.setDeletado(false);

        endereco = new Endereco();
        endereco.setId(1L);
        endereco.setCep("12345-678");
        endereco.setCidade("Cidade Teste");
        endereco.setDescricao("Descrição Teste");
        endereco.setNumero(100);
        endereco.setDeletado(false);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    @DisplayName("adicionar")
    class AdicionarTests {
        @Test
        @DisplayName("Deve adicionar um endereço e associar o cliente")
        void adicionarEnderecoSucesso() {
            when(clienteService.encontrarPeloID(1L)).thenReturn(cliente);
            when(enderecoRepository.save(any(Endereco.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Endereco result = enderecoService.adicionar(1L, endereco);

            verify(clienteService, times(1)).encontrarPeloID(1L);
            verify(enderecoRepository, times(1)).save(endereco);
            assertThat(result.getCliente()).isEqualTo(cliente);
        }
        @Test
        @DisplayName("Deve lançar NullPointerException se o código do cliente for nulo")
        void adicionarEnderecoSemCliente() {
            assertThatThrownBy(() -> enderecoService.findByClient(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("ID não pode ser nulo");
        }
    }

    @Nested
    @DisplayName("atualizar")
    class AtualizarTests {
        @Test
        @DisplayName("Deve atualizar um endereço com sucesso")
        void atualizarEnderecoSucesso() {
            // Endereço existente a ser atualizado
            Endereco enderecoExistente = new Endereco();
            enderecoExistente.setId(1L);
            enderecoExistente.setCep("00000-000");
            enderecoExistente.setCidade("Cidade Antiga");
            enderecoExistente.setDescricao("Descrição Antiga");
            enderecoExistente.setNumero(10);
            enderecoExistente.setDeletado(false);

            // Novos dados para atualização
            Endereco enderecoNovosDados = new Endereco();
            enderecoNovosDados.setCep("12345-678");
            enderecoNovosDados.setCidade("Cidade Nova");
            enderecoNovosDados.setDescricao("Descrição Nova");
            enderecoNovosDados.setNumero(200);

            when(enderecoRepository.findById(1L)).thenReturn(Optional.of(enderecoExistente));
            when(enderecoRepository.save(any(Endereco.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Endereco result = enderecoService.atualizar(1L, enderecoNovosDados);

            assertThat(result.getCep()).isEqualTo("12345-678");
            assertThat(result.getCidade()).isEqualTo("Cidade Nova");
            assertThat(result.getDescricao()).isEqualTo("Descrição Nova");
            assertThat(result.getNumero()).isEqualTo(200);
            verify(enderecoRepository, times(1)).findById(1L);
            verify(enderecoRepository, times(1)).save(enderecoExistente);
        }

        @Test
        @DisplayName("Deve lançar NullPointerException se o ID for nulo")
        void atualizarEnderecoComIdNulo() {
            Endereco bobao = new Endereco();
            assertThatThrownBy(() -> enderecoService.atualizar(null, bobao))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("ID não pode ser nulo");
        }

        @Test
        @DisplayName("Deve lançar RegistroNotFoundException se o endereço não for encontrado")
        void atualizarEnderecoNaoEncontrado() {
            Endereco bobao = new Endereco();
            when(enderecoRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> enderecoService.atualizar(1L, bobao))
                    .isInstanceOf(RegistroNotFoundException.class)
                    .hasMessageContaining("Endereço não encontrado com este ID!");
        }
    }

    @Nested
    @DisplayName("findByClient")
    class FindByClientTests {
        @Test
        @DisplayName("Deve retornar a lista de endereços do cliente")
        void findByClientSucesso() {
            List<Endereco> lista = List.of(endereco);
            when(clienteService.encontrarPeloID(1L)).thenReturn(cliente);
            when(enderecoRepository.findEnderecoByCliente_Id(1L)).thenReturn(lista);

            List<Endereco> result = enderecoService.findByClient(1L);

            assertThat(result).hasSize(1).contains(endereco);
            verify(clienteService, times(1)).encontrarPeloID(1L);
            verify(enderecoRepository, times(1)).findEnderecoByCliente_Id(1L);
        }

        @Test
        @DisplayName("Deve lançar NullPointerException se o código do cliente for nulo")
        void findByClientComCodigoNulo() {
            assertThatThrownBy(() -> enderecoService.findByClient(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("ID não pode ser nulo");
        }
    }

    @Nested
    @DisplayName("buscarEnderecoPorId")
    class BuscarEnderecoPorIdTests {
        @Test
        @DisplayName("Deve retornar o endereço se encontrado")
        void buscarEnderecoPorIdSucesso() {
            when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));

            Endereco result = enderecoService.buscarEnderecoPorId(1L);

            assertThat(result).isEqualTo(endereco);
            verify(enderecoRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Deve lançar NullPointerException se o ID for nulo")
        void buscarEnderecoPorIdComIdNulo() {
            assertThatThrownBy(() -> enderecoService.buscarEnderecoPorId(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("ID não pode ser nulo");
        }

        @Test
        @DisplayName("Deve lançar RegistroNotFoundException se o endereço não for encontrado")
        void buscarEnderecoPorIdNaoEncontrado() {
            when(enderecoRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> enderecoService.buscarEnderecoPorId(1L))
                    .isInstanceOf(RegistroNotFoundException.class)
                    .hasMessageContaining("Endereço não encontrado com este ID!");
        }
    }

    @Nested
    @DisplayName("remover")
    class RemoverTests {
        @Test
        @DisplayName("Deve marcar o endereço como deletado com sucesso")
        void removerEnderecoSucesso() {
            endereco.setDeletado(true);
            when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));
            when(enderecoRepository.save(any(Endereco.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Endereco result = enderecoService.remover(1L);

            assertThat(result.isDeletado()).isTrue();
            verify(enderecoRepository, times(1)).findById(1L);
            verify(enderecoRepository, times(1)).save(endereco);
        }

        @Test
        @DisplayName("Deve lançar NullPointerException se o ID for nulo")
        void removerEnderecoComIdNulo() {
            assertThatThrownBy(() -> enderecoService.remover(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("ID não pode ser nulo");
        }

        @Test
        @DisplayName("Deve lançar RegistroNotFoundException se o endereço não for encontrado")
        void removerEnderecoNaoEncontrado() {
            when(enderecoRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> enderecoService.remover(1L))
                    .isInstanceOf(RegistroNotFoundException.class)
                    .hasMessageContaining("Endereço não encontrado com este ID!");
        }
    }
}
