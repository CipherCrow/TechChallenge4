package br.com.techchallenge.safedeliver.gerenciamentoentregas.service;

import br.com.techchallenge.safedeliver.gerenciamentoentregas.client.EnderecoClient;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.client.PedidoClient;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Endereco;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Pedido;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Rastreamento;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.enums.StatusPedidoEnum;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.dto.EnderecoDTO;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.dto.PedidoDTO;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.exception.ComunicacaoException;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.exception.RegistroNotFoundException;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.mapper.PedidoMapper;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.repository.RastreamentoRepository;
import feign.FeignException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RastreamentoServiceTest {

    private RastreamentoServiceImpl rastreamentoService;

    @Mock
    private RastreamentoRepository rastreamentoRepository;

    @Mock
    private EnderecoClient enderecoClient;

    @Mock
    private PedidoClient pedidoClient;

    private AutoCloseable openMocks;

    private Pedido pedidoConfirmado;
    private Endereco enderecoValido;
    private Rastreamento rastreio;

    // Para simular respostas dos clients
    private PedidoDTO pedidoDTO;
    private EnderecoDTO enderecoDTO;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        rastreamentoService = new RastreamentoServiceImpl(rastreamentoRepository,enderecoClient,pedidoClient);

        pedidoConfirmado = Pedido.builder()
                .id(1L)
                .statusPedido(StatusPedidoEnum.CONFIRMADO)
                .build();

        enderecoValido = Endereco.builder()
                .id(2L)
                .cep("12345-678")
                .build();

        rastreio = Rastreamento.builder()
                .id(10L)
                .pedido(pedidoConfirmado)
                .endereco(enderecoValido)
                .build();

        pedidoDTO = PedidoMapper.toDTO(pedidoConfirmado);
        enderecoDTO = new EnderecoDTO(enderecoValido.getId(), enderecoValido.getCep(), "Cidade Exemplo", "Descricao", 100, null, false);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    class CriarRastreioTests {

        @Test
        void criarRastreioSucesso() {
            when(pedidoClient.pegarPedido(1L)).thenReturn(pedidoDTO);
            when(enderecoClient.encontrarEndereco(2L)).thenReturn(enderecoDTO);
            when(rastreamentoRepository.save(any(Rastreamento.class)))
                    .thenAnswer(invocation -> {
                        Rastreamento r = invocation.getArgument(0);
                        r.setId(100L);
                        return r;
                    });

            Rastreamento result = rastreamentoService.criarRastreio(1L, 2L);

            assertThat(result.getId()).isEqualTo(100L);
            assertThat(result.getPedido().getId()).isEqualTo(1L);
            assertThat(result.getEndereco().getId()).isEqualTo(2L);
            verify(pedidoClient, times(1)).pegarPedido(1L);
            verify(enderecoClient, times(1)).encontrarEndereco(2L);
            verify(rastreamentoRepository, times(1)).save(any(Rastreamento.class));
        }

        @Test
        void criarRastreioPedidoNaoConfirmado() {
            Pedido pedidoNaoConfirmado = Pedido.builder()
                    .id(2L)
                    .statusPedido(StatusPedidoEnum.EM_ANDAMENTO)
                    .build();

            PedidoDTO pedidoDTONaoConfirmado = PedidoMapper.toDTO(pedidoNaoConfirmado);
            when(pedidoClient.pegarPedido(2L)).thenReturn(pedidoDTONaoConfirmado);

            assertThatThrownBy(() -> rastreamentoService.criarRastreio(2L, 2L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Só é possível criar rastreio para pedidos confirmados!");
            verify(pedidoClient, times(1)).pegarPedido(2L);
            verifyNoInteractions(enderecoClient);
            verify(rastreamentoRepository, never()).save(any(Rastreamento.class));
        }

        @Test
        void criarRastreioPedidoNotFound() {
            FeignException notFound = mock(FeignException.NotFound.class);
            when(pedidoClient.pegarPedido(1L)).thenThrow(notFound);

            assertThatThrownBy(() -> rastreamentoService.criarRastreio(1L, 2L))
                    .isInstanceOf(RegistroNotFoundException.class)
                    .hasMessageContaining("Pedido não encontrado com este ID!");
            verify(pedidoClient, times(1)).pegarPedido(1L);
            verifyNoInteractions(enderecoClient);
            verify(rastreamentoRepository, never()).save(any(Rastreamento.class));
        }

        @Test
        void criarRastreioPedidoComunicacaoException() {
            FeignException generic = mock(FeignException.class);
            when(pedidoClient.pegarPedido(1L)).thenThrow(generic);

            assertThatThrownBy(() -> rastreamentoService.criarRastreio(1L, 2L))
                    .isInstanceOf(ComunicacaoException.class)
                    .hasMessage("Erro ao comunicar com o serviço de Pedido");
            verify(pedidoClient, times(1)).pegarPedido(1L);
            verifyNoInteractions(enderecoClient);
            verify(rastreamentoRepository, never()).save(any(Rastreamento.class));
        }

        @Test
        void criarRastreioEnderecoNotFound() {
            when(pedidoClient.pegarPedido(1L)).thenReturn(pedidoDTO);
            FeignException notFound = mock(FeignException.NotFound.class);
            when(enderecoClient.encontrarEndereco(2L)).thenThrow(notFound);

            assertThatThrownBy(() -> rastreamentoService.criarRastreio(1L, 2L))
                    .isInstanceOf(RegistroNotFoundException.class)
                    .hasMessageContaining("Endereco não encontrado com este ID!");
            verify(pedidoClient, times(1)).pegarPedido(1L);
            verify(enderecoClient, times(1)).encontrarEndereco(2L);
            verify(rastreamentoRepository, never()).save(any(Rastreamento.class));
        }

        @Test
        void criarRastreioEnderecoComunicacaoException() {
            when(pedidoClient.pegarPedido(1L)).thenReturn(pedidoDTO);
            FeignException generic = mock(FeignException.class);
            when(enderecoClient.encontrarEndereco(2L)).thenThrow(generic);

            assertThatThrownBy(() -> rastreamentoService.criarRastreio(1L, 2L))
                    .isInstanceOf(ComunicacaoException.class)
                    .hasMessage("Erro ao comunicar com o serviço de Endereco");
            verify(pedidoClient, times(1)).pegarPedido(1L);
            verify(enderecoClient, times(1)).encontrarEndereco(2L);
            verify(rastreamentoRepository, never()).save(any(Rastreamento.class));
        }
    }

    @Nested
    class EncontrarPeloIdTests {

        @Test
        void encontrarPeloIdSucesso() {
            when(rastreamentoRepository.findById(10L)).thenReturn(Optional.of(rastreio));

            Rastreamento result = rastreamentoService.encontrarPeloId(10L);

            assertThat(result).isEqualTo(rastreio);
            verify(rastreamentoRepository, times(1)).findById(10L);
        }

        @Test
        void encontrarPeloIdNulo() {
            assertThatThrownBy(() -> rastreamentoService.encontrarPeloId(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("ID não pode ser nulo");
            verify(rastreamentoRepository, never()).findById(any());
        }

        @Test
        void encontrarPeloIdNaoEncontrado() {
            when(rastreamentoRepository.findById(10L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> rastreamentoService.encontrarPeloId(10L))
                    .isInstanceOf(RegistroNotFoundException.class)
                    .hasMessageContaining("Rastreamento");
            verify(rastreamentoRepository, times(1)).findById(10L);
        }
    }

    @Nested
    class EncontrarAgruparPeloCepTests {

        @Test
        void encontrarAgruparPeloCepSucesso() {
            List<Rastreamento> list = List.of(rastreio);
            when(rastreamentoRepository.findRastreamentoByEndereco_CepLike("12345"))
                    .thenReturn(Optional.of(list));

            List<Rastreamento> result = rastreamentoService.encontrarAgruparPeloCep("12345");

            assertThat(result).isEqualTo(list);
            verify(rastreamentoRepository, times(1)).findRastreamentoByEndereco_CepLike("12345");
        }

        @Test
        void encontrarAgruparPeloCepNulo() {
            assertThatThrownBy(() -> rastreamentoService.encontrarAgruparPeloCep(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("ID não pode ser nulo");
            verify(rastreamentoRepository, never()).findRastreamentoByEndereco_CepLike(any());
        }

        @Test
        void encontrarAgruparPeloCepNaoEncontrado() {
            when(rastreamentoRepository.findRastreamentoByEndereco_CepLike("12345"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> rastreamentoService.encontrarAgruparPeloCep("12345"))
                    .isInstanceOf(RegistroNotFoundException.class)
                    .hasMessageContaining("Rastreamento");
            verify(rastreamentoRepository, times(1)).findRastreamentoByEndereco_CepLike("12345");
        }
    }

    @Nested
    class EncontrarPedidoTests {

        @Test
        void encontrarPedidoSucesso() {
            when(pedidoClient.pegarPedido(1L)).thenReturn(pedidoDTO);

            Pedido result = rastreamentoService.encontrarPedido(1L);

            assertThat(result.getId()).isEqualTo(pedidoConfirmado.getId());
            assertThat(result.getStatusPedido()).isEqualTo(pedidoConfirmado.getStatusPedido());
            verify(pedidoClient, times(1)).pegarPedido(1L);
        }

        @Test
        void encontrarPedidoNotFound() {
            FeignException notFound = mock(FeignException.NotFound.class);
            when(pedidoClient.pegarPedido(1L)).thenThrow(notFound);

            assertThatThrownBy(() -> rastreamentoService.encontrarPedido(1L))
                    .isInstanceOf(RegistroNotFoundException.class)
                    .hasMessageContaining("Pedido");
            verify(pedidoClient, times(1)).pegarPedido(1L);
        }

        @Test
        void encontrarPedidoComunicacaoException() {
            FeignException generic = mock(FeignException.class);
            when(pedidoClient.pegarPedido(1L)).thenThrow(generic);

            assertThatThrownBy(() -> rastreamentoService.encontrarPedido(1L))
                    .isInstanceOf(ComunicacaoException.class)
                    .hasMessage("Erro ao comunicar com o serviço de Pedido");
            verify(pedidoClient, times(1)).pegarPedido(1L);
        }
    }

    @Nested
    class EncontrarEnderecoTests {

        @Test
        void encontrarEnderecoSucesso() {
            when(enderecoClient.encontrarEndereco(2L)).thenReturn(enderecoDTO);

            Endereco result = rastreamentoService.encontrarEndereco(2L);

            assertThat(result.getId()).isEqualTo(enderecoValido.getId());
            assertThat(result.getCep()).isEqualTo(enderecoValido.getCep());
            verify(enderecoClient, times(1)).encontrarEndereco(2L);
        }

        @Test
        void encontrarEnderecoNotFound() {
            FeignException notFound = mock(FeignException.NotFound.class);
            when(enderecoClient.encontrarEndereco(2L)).thenThrow(notFound);

            assertThatThrownBy(() -> rastreamentoService.encontrarEndereco(2L))
                    .isInstanceOf(RegistroNotFoundException.class)
                    .hasMessageContaining("Endereco não encontrado com este ID!");
            verify(enderecoClient, times(1)).encontrarEndereco(2L);
        }

        @Test
        void encontrarEnderecoComunicacaoException() {
            FeignException generic = mock(FeignException.class);
            when(enderecoClient.encontrarEndereco(2L)).thenThrow(generic);

            assertThatThrownBy(() -> rastreamentoService.encontrarEndereco(2L))
                    .isInstanceOf(ComunicacaoException.class)
                    .hasMessage("Erro ao comunicar com o serviço de Endereco");
            verify(enderecoClient, times(1)).encontrarEndereco(2L);
        }
    }
}
