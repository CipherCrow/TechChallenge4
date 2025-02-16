package br.com.techchallenge.safedeliver.gerenciamentoentregas.service;

import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Localizacao;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Rastreamento;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.exception.RegistroNotFoundException;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.repository.LocalizacaoRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LocalizacaoServiceTest {

    private LocalizacaoServiceImpl localizacaoService;

    @Mock
    private LocalizacaoRepository localizacaoRepository;

    @Mock
    private RastreamentoService rastreamentoService;

    private AutoCloseable openMocks;

    private Localizacao localizacao;
    private Rastreamento rastreamento;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        localizacaoService = new LocalizacaoServiceImpl(localizacaoRepository, rastreamentoService);

        localizacao = new Localizacao();
        localizacao.setId(1L);
        localizacao.setLatitude("20.0000");
        localizacao.setLongitude("10.0000");
        localizacao.setHoraRegistro(LocalDateTime.now());
        localizacao.setRastreamento(null);

        rastreamento = Rastreamento.builder()
                .id(100L)
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    @DisplayName("adicionar")
    class AdicionarTests {
        @Test
        @DisplayName("Deve adicionar a localização e associar o rastreamento")
        void adicionarSucesso() {
            when(rastreamentoService.encontrarPeloId(100L)).thenReturn(rastreamento);
            when(localizacaoRepository.save(any(Localizacao.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Localizacao resultado = localizacaoService.adicionar(localizacao, 100L);

            assertThat(resultado.getRastreamento()).isEqualTo(rastreamento);
            verify(rastreamentoService, times(1)).encontrarPeloId(100L);
            verify(localizacaoRepository, times(1)).save(localizacao);
        }
    }

    @Nested
    @DisplayName("buscarPeloRastreamento")
    class BuscarPeloRastreamentoTests {

        @Test
        @DisplayName("Deve retornar a lista de localizações para um rastreamento existente")
        void buscarPeloRastreamentoSucesso() {
            List<Localizacao> lista = List.of(localizacao);
            when(localizacaoRepository.findLocalizacaosByRastreamento_Id(100L))
                    .thenReturn(Optional.of(lista));

            List<Localizacao> resultado = localizacaoService.buscarPeloRastreamento(100L);

            assertThat(resultado).isNotEmpty().hasSize(1);
            assertThat(resultado.get(0)).isEqualTo(localizacao);
            verify(localizacaoRepository, times(1)).findLocalizacaosByRastreamento_Id(100L);
        }

        @Test
        @DisplayName("Deve lançar NullPointerException se o código do rastreamento for nulo")
        void buscarPeloRastreamentoComIdNulo() {
            assertThatThrownBy(() -> localizacaoService.buscarPeloRastreamento(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("ID não pode ser nulo");
        }

        @Test
        @DisplayName("Deve lançar RegistroNotFoundException se não houver localizações para o rastreamento")
        void buscarPeloRastreamentoNaoEncontrado() {
            when(localizacaoRepository.findLocalizacaosByRastreamento_Id(100L))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> localizacaoService.buscarPeloRastreamento(100L))
                    .isInstanceOf(RegistroNotFoundException.class)
                    .hasMessageContaining("Localizacao");
        }
    }
}
