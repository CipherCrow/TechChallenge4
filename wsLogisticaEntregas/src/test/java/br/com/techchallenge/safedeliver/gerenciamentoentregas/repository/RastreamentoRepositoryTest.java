package br.com.techchallenge.safedeliver.gerenciamentoentregas.repository;

import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Endereco;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Pedido;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Rastreamento;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RastreamentoRepositoryTest {

    @Mock
    private RastreamentoRepository rastreamentoRepository;

    AutoCloseable openMocks;

    private Pedido pedido =
            Pedido.builder()
                .id(1L)
                .build();

    private Endereco endereco =
            Endereco.builder()
                    .id(1L)
                    .cep("57304-563")
                    .build();

    private List<Rastreamento> rastreamentos = Arrays.asList(
            new Rastreamento(1L, pedido, endereco),
            new Rastreamento(2L, Pedido.builder().id(2L).build(), endereco)
    );

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception{
        openMocks.close();
    }

    @Nested
    @DisplayName("buscarRastreamentos")
    class buscarRastreamentos {
        @Test
        @DisplayName("Deve buscar uma lista com sucesso!")
        void buscarRastreamentosPeloCepLike() {
            when(rastreamentoRepository.findRastreamentoByEndereco_CepLike("57304"))
                    .thenReturn(Optional.ofNullable(rastreamentos));

            Optional<List<Rastreamento>> result = rastreamentoRepository.findRastreamentoByEndereco_CepLike("57304");

            assertThat(result).isNotEmpty();
            assertThat(result.get()).hasSize(2);
            verify(rastreamentoRepository, times(1)).findRastreamentoByEndereco_CepLike("57304");
        }
    }
}