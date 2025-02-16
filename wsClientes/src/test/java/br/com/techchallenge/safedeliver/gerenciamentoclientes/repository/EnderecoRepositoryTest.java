package br.com.techchallenge.safedeliver.gerenciamentoclientes.repository;

import br.com.techchallenge.safedeliver.gerenciamentoclientes.domain.model.entities.Cliente;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.domain.model.entities.Endereco;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EnderecoRepositoryTest {

    @Mock
    private EnderecoRepository enderecoRepository;

    AutoCloseable openMocks;

    private List<Endereco> enderecosDoCliente = Arrays.asList(
            new Endereco(1L, "12345-678", "Cidade Teste", "Descrição Teste", 100,null,false)
    );

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        cliente = new Cliente();
        cliente.setId(1L);
        enderecosDoCliente.get(0).setCliente(cliente);
    }

    @AfterEach
    void tearDown() throws Exception{
        openMocks.close();
    }

    @Nested
    @DisplayName("buscarEnderecosDoCliente")
    class CriarTests {
        @Test
        @DisplayName("Deve buscar uma lista com sucesso!")
        void encontrarEnderecosDoCliente() {
            when(enderecoRepository.findEnderecoByCliente_Id(1L)).thenReturn(enderecosDoCliente);

            List<Endereco> result = enderecoRepository.findEnderecoByCliente_Id(1L);

            assertThat(result).hasSize(1).containsAll(enderecosDoCliente);
            verify(enderecoRepository, times(1)).findEnderecoByCliente_Id(1L);
        }
    }
}