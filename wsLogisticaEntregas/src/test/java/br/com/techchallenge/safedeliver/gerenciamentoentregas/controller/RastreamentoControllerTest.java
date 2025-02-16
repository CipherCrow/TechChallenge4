package br.com.techchallenge.safedeliver.gerenciamentoentregas.controller;

import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Endereco;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Localizacao;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Pedido;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Rastreamento;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.enums.StatusPedidoEnum;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.dto.LocalizacaoDTO;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.dto.RastreamentoDTO;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.exception.RegistroNotFoundException;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.mapper.LocalizacaoMapper;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.mapper.RastreamentoMapper;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.service.LocalizacaoService;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.service.RastreamentoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RastreamentoControllerTest {

    @InjectMocks
    private RastreamentoController rastreamentoController;

    @Mock
    private RastreamentoService rastreamentoService;

    @Mock
    private LocalizacaoService localizacaoService;

    private MockMvc mockMvc;

    private AutoCloseable openMocks;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Pedido pedidoConfirmado;
    private Rastreamento rastreamento;
    private RastreamentoDTO rastreamentoDTO;
    private Localizacao localizacao;
    private LocalizacaoDTO localizacaoDTO;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        RastreamentoController rastreamentoController =
                new RastreamentoController(rastreamentoService, localizacaoService);
        mockMvc = MockMvcBuilders.standaloneSetup(rastreamentoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        // Configurar ObjectMapper para tratar as datas pq tava dando erro
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        pedidoConfirmado = Pedido.builder()
                .id(2L)
                .statusPedido(StatusPedidoEnum.CONFIRMADO)
                .build();

        rastreamento = Rastreamento.builder()
                .id(1L)
                .endereco(
                        Endereco.builder().cep("12346-001").build()
                )
                .pedido(pedidoConfirmado)
                .build();

        rastreamentoDTO = RastreamentoMapper.toDTO(rastreamento);

        // Configura objeto dummy para Localizacao
        localizacao = Localizacao.builder()
                .id(20L)
                .latitude("20.0000")
                .longitude("10.0000")
                .horaRegistro(LocalDateTime.now())
                .rastreamento(rastreamento)
                .build();

        localizacaoDTO = LocalizacaoMapper.toDTO(localizacao);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    @DisplayName("POST /rastreamento/criarRastreamento")
    class CriarRastreamentoTests {

        @Test
        @DisplayName("Deve criar rastreamento com sucesso")
        void criarRastreamentoSucesso() throws Exception {
            when(rastreamentoService.criarRastreio(eq(1L), eq(2L)))
                    .thenReturn(rastreamento);

            mockMvc.perform(post("/rastreamento/criarRastreamento")
                            .param("codPedido", "1")
                            .param("codEndereco", "2"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(rastreamento.getId()))
                    .andExpect(jsonPath("$.pedido.id").value(pedidoConfirmado.getId()));
            verify(rastreamentoService, times(1)).criarRastreio(1L, 2L);
        }

        @Test
        @DisplayName("Deve retornar BAD_REQUEST a passar um pedido não confimado")
        void criarRastreamentoErro() throws Exception {
            when(rastreamentoService.criarRastreio(eq(1L), eq(2L)))
                    .thenThrow(new IllegalArgumentException("Só é possível criar rastreio para pedidos confirmados!"));

            mockMvc.perform(post("/rastreamento/criarRastreamento")
                            .param("codPedido", "1")
                            .param("codEndereco", "2"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Só é possível criar rastreio para pedidos confirmados!"));
        }
    }

    @Nested
    @DisplayName("GET /rastreamento/buscarRastreamento/{id}")
    class BuscarRastreamentoTests {

        @Test
        @DisplayName("Deve retornar rastreamento por ID com sucesso")
        void buscarRastreamentoSucesso() throws Exception {
            when(rastreamentoService.encontrarPeloId(1L))
                    .thenReturn(rastreamento);

            mockMvc.perform(get("/rastreamento/buscarRastreamento/{id}", 1L))
                    .andExpect(status().isFound())
                    .andExpect(jsonPath("$.id").value(rastreamento.getId()))
                    .andExpect(jsonPath("$.pedido.id").value(pedidoConfirmado.getId()));
            verify(rastreamentoService, times(1)).encontrarPeloId(1L);
        }

        @Test
        @DisplayName("Deve retornar NOT_FOUND quando rastreamento não for encontrado")
        void buscarRastreamentoNaoEncontrado() throws Exception {
            when(rastreamentoService.encontrarPeloId(10L))
                    .thenThrow(new RegistroNotFoundException("Rastreamento"));

            mockMvc.perform(get("/rastreamento/buscarRastreamento/{id}", 10L))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Rastreamento não encontrado com este ID!"));
            verify(rastreamentoService, times(1)).encontrarPeloId(10L);
        }
    }

    @Nested
    @DisplayName("GET /rastreamento/buscarRastreamentoCep/{cep}")
    class BuscarRastreamentoCepTests {

        @Test
        @DisplayName("Deve retornar lista de rastreamentos pelo CEP com sucesso")
        void buscarRastreamentoCepSucesso() throws Exception {
            List<Rastreamento> lista = List.of(rastreamento);
            when(rastreamentoService.encontrarAgruparPeloCep("12345")).thenReturn(lista);

            mockMvc.perform(get("/rastreamento/buscarRastreamentoCep/{cep}", "12345"))
                    .andExpect(status().isFound())
                    .andExpect(jsonPath("$[0].id").value(rastreamento.getId()));
            verify(rastreamentoService, times(1)).encontrarAgruparPeloCep("12345");
        }

        @Test
        @DisplayName("Deve retornar NOT_FOUND quando nenhum rastreamento for encontrado para o CEP")
        void buscarRastreamentoCepNaoEncontrado() throws Exception {
            when(rastreamentoService.encontrarAgruparPeloCep("52345"))
                    .thenThrow(new RegistroNotFoundException("Rastreamento"));

            mockMvc.perform(get("/rastreamento/buscarRastreamentoCep/{cep}", "52345"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Rastreamento não encontrado com este ID!"));
            verify(rastreamentoService, times(1)).encontrarAgruparPeloCep("52345");
        }
    }

    @Nested
    @DisplayName("POST /rastreamento/adicionarLocalizacao/{id}")
    class AdicionarLocalizacaoTests {

        @Test
        @DisplayName("Deve adicionar localização com sucesso")
        void adicionarLocalizacaoSucesso() throws Exception {
            when(localizacaoService.adicionar(any(Localizacao.class), eq(1L)))
                    .thenReturn(localizacao);

            String json = objectMapper.writeValueAsString(localizacaoDTO);

            mockMvc.perform(post("/rastreamento/adicionarLocalizacao/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(localizacao.getId()))
                    .andExpect(jsonPath("$.latitude").value(localizacao.getLatitude()))
                    .andExpect(jsonPath("$.longitude").value(localizacao.getLongitude()));
            verify(localizacaoService, times(1)).adicionar(any(Localizacao.class), eq(1L));
        }

        @Test
        @DisplayName("Deve retornar NOT_FOUND quando passar rastreamento que não existe")
        void adicionarLocalizacaoErro() throws Exception {
            when(localizacaoService.adicionar(any(Localizacao.class), eq(10L)))
                    .thenThrow(new RegistroNotFoundException("Rastreamento"));

            String json = objectMapper.writeValueAsString(localizacaoDTO);

            mockMvc.perform(post("/rastreamento/adicionarLocalizacao/{id}", 10L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Rastreamento não encontrado com este ID!"));
            verify(localizacaoService, times(1)).adicionar(any(Localizacao.class), eq(10L));
        }
    }

    @Nested
    @DisplayName("GET /rastreamento/visualizarHistorico/{id}")
    class VisualizarHistoricoTests {

        @Test
        @DisplayName("Deve retornar histórico de localização com sucesso")
        void visualizarHistoricoSucesso() throws Exception {
            List<Localizacao> lista = List.of(localizacao);
            when(localizacaoService.buscarPeloRastreamento(1L)).thenReturn(lista);

            mockMvc.perform(get("/rastreamento/visualizarHistorico/{id}", 1L))
                    .andExpect(status().isFound())
                    .andExpect(jsonPath("$[0].id").value(localizacao.getId()));
            verify(localizacaoService, times(1)).buscarPeloRastreamento(1L);
        }

        @Test
        @DisplayName("Deve retornar NOT_FOUND quando não existir localizacao para aquele rastreamento")
        void visualizarHistoricoErro() throws Exception {
            when(localizacaoService.buscarPeloRastreamento(10L))
                    .thenThrow(new RegistroNotFoundException("Localizacao"));

            mockMvc.perform(get("/rastreamento/visualizarHistorico/{id}", 10L))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Localizacao não encontrado com este ID!"));
            verify(localizacaoService, times(1)).buscarPeloRastreamento(10L);
        }
    }
}
