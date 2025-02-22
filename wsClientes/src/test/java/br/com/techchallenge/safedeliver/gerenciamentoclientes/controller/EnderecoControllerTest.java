package br.com.techchallenge.safedeliver.gerenciamentoclientes.controller;

import br.com.techchallenge.safedeliver.gerenciamentoclientes.domain.model.entities.Endereco;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.exception.RegistroNotFoundException;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.service.EnderecoService;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EnderecoControllerTest {

    @Mock
    private EnderecoService enderecoService;

    private MockMvc mockMvc;

    private AutoCloseable openMocks;

    private Endereco endereco;
    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        EnderecoController enderecoController = new EnderecoController(enderecoService);
        mockMvc = MockMvcBuilders.standaloneSetup(enderecoController).build();

        endereco = new Endereco();
        endereco.setId(1L);
        endereco.setCep("12345-678");
        endereco.setCidade("Cidade Teste");
        endereco.setDescricao("Endereco Teste");
        endereco.setNumero(100);
        endereco.setDeletado(false);
        endereco.setCliente(null);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    @DisplayName("GET /enderecos/cliente/{clienteId}")
    class EncontrarPeloClienteEndpointTests {

        @Test
        @DisplayName("Deve retornar a lista de enderecos do cliente com sucesso")
        void encontrarPeloClienteSucesso() throws Exception {
            List<Endereco> lista = List.of(endereco);
            when(enderecoService.findByClient(1L)).thenReturn(lista);

            mockMvc.perform(get("/enderecos/cliente/{clienteId}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(endereco.getId()))
                    .andExpect(jsonPath("$[0].cep").value(endereco.getCep()))
                    .andExpect(jsonPath("$[0].cidade").value(endereco.getCidade()))
                    .andExpect(jsonPath("$[0].descricao").value(endereco.getDescricao()))
                    .andExpect(jsonPath("$[0].numero").value(endereco.getNumero()))
                    .andExpect(jsonPath("$[0].deletado").value(endereco.isDeletado()));
        }

        @Test
        @DisplayName("Deve retornar NOT_FOUND quando não achar o cliente")
        void encontrarPeloClienteNotFound() throws Exception {
            when(enderecoService.findByClient(1L)).thenThrow(new RegistroNotFoundException("Cliente"));

            mockMvc.perform(get("/enderecos/cliente/{clienteId}", 1L))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Cliente não encontrado com este ID!"));
        }
    }
}
