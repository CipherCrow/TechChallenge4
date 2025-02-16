package br.com.techchallenge.safedeliver.gerenciamentoentregas.repository;

import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Localizacao;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Rastreamento;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LocalizacaoRepositoryTest {

    @Mock
    private LocalizacaoRepository localizacaoRepository;

    AutoCloseable openMocks;

    private Rastreamento rastreia =
            Rastreamento.builder()
                .id(1L)
                .build();

    private List<Localizacao> localizacoes = Arrays.asList(
            new Localizacao(1L, "longitude", "late", LocalDateTime.now(), rastreia),
            new Localizacao(2L, "longitude", "auau", LocalDateTime.now(), rastreia)
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
    @DisplayName("buscarAsLocalizacoesDoCliente")
    class buscarLocalizacoesDoCliente {
        @Test
        @DisplayName("Deve buscar uma lista com sucesso!")
        void localizacoesDoCliente() {
            when(localizacaoRepository.findLocalizacaosByRastreamento_Id(rastreia.getId()))
                    .thenReturn(Optional.ofNullable(localizacoes));

            Optional<List<Localizacao>> result = localizacaoRepository.findLocalizacaosByRastreamento_Id(rastreia.getId());

            assertThat(result).isNotEmpty();
            verify(localizacaoRepository, times(1)).findLocalizacaosByRastreamento_Id(rastreia.getId());
        }
    }
}