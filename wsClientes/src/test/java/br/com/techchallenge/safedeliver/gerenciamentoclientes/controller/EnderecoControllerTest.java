package br.com.techchallenge.safedeliver.gerenciamentoclientes.controller;

import br.com.techchallenge.safedeliver.gerenciamentoclientes.domain.model.entities.Endereco;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.dto.EnderecoDTO;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.exception.RegistroNotFoundException;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.mapper.EnderecoMapper;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.service.EnderecoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EnderecoControllerTest {

    @Mock
    private EnderecoService enderecoService;

    private MockMvc mockMvc;

    private AutoCloseable openMocks;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Endereco endereco;
    private EnderecoDTO enderecoDTO;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        EnderecoController enderecoController = new EnderecoController(enderecoService);
        mockMvc = MockMvcBuilders.standaloneSetup(enderecoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        endereco = new Endereco();
        endereco.setId(1L);
        endereco.setCep("12345-678");
        endereco.setCidade("Cidade Teste");
        endereco.setDescricao("Endereco Teste");
        endereco.setNumero(100);
        endereco.setDeletado(false);
        endereco.setCliente(null);

        enderecoDTO = EnderecoMapper.toDTO(endereco);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    @DisplayName("POST /endereco/criar")
    class CriarEndpointTests {

        @Test
        @DisplayName("Deve criar o endereço com sucesso")
        void criarEnderecoSucesso() throws Exception {
            when(enderecoService.adicionar(eq(1L), any(Endereco.class))).thenReturn(endereco);

            String jsonContent = objectMapper.writeValueAsString(enderecoDTO);

            mockMvc.perform(post("/endereco/criar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoCliente", "1")
                            .content(jsonContent))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(endereco.getId()))
                    .andExpect(jsonPath("$.cep").value(endereco.getCep()))
                    .andExpect(jsonPath("$.cidade").value(endereco.getCidade()))
                    .andExpect(jsonPath("$.descricao").value(endereco.getDescricao()))
                    .andExpect(jsonPath("$.numero").value(endereco.getNumero()))
                    .andExpect(jsonPath("$.deletado").value(endereco.isDeletado()));
        }

        @Test
        @DisplayName("Deve retornar NOT_FOUND quando o cliente não for encontrado")
        void criarEnderecoClienteNaoEncontrado() throws Exception {
            EnderecoDTO novoDTO = new EnderecoDTO(1L, "54321-000", "Nova Cidade", "Novo Endereco", 200, null, false);
            when(enderecoService.adicionar(eq(1L), any(Endereco.class)))
                    .thenThrow(new RegistroNotFoundException("Cliente"));

            String jsonContent = objectMapper.writeValueAsString(novoDTO);

            mockMvc.perform(post("/endereco/criar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoCliente", "1")
                            .content(jsonContent))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Cliente não encontrado com este ID!"));
        }

    }

    @Nested
    @DisplayName("PUT /endereco/atualizar/{id}")
    class AtualizarEndpointTests {

        @Test
        @DisplayName("Deve atualizar o endereço com sucesso")
        void atualizarEnderecoSucesso() throws Exception {
            EnderecoDTO novoDTO = new EnderecoDTO(1L, "54321-000", "Nova Cidade", "Novo Endereco", 200, null, false);
            Endereco enderecoAtualizado = new Endereco();
            enderecoAtualizado.setId(1L);
            enderecoAtualizado.setCep("54321-000");
            enderecoAtualizado.setCidade("Nova Cidade");
            enderecoAtualizado.setDescricao("Novo Endereco");
            enderecoAtualizado.setNumero(200);
            enderecoAtualizado.setDeletado(false);
            enderecoAtualizado.setCliente(null);

            when(enderecoService.atualizar(eq(1L), any(Endereco.class))).thenReturn(enderecoAtualizado);

            String jsonContent = objectMapper.writeValueAsString(novoDTO);

            mockMvc.perform(put("/endereco/atualizar/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonContent))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(enderecoAtualizado.getId()))
                    .andExpect(jsonPath("$.cep").value(enderecoAtualizado.getCep()))
                    .andExpect(jsonPath("$.cidade").value(enderecoAtualizado.getCidade()))
                    .andExpect(jsonPath("$.descricao").value(enderecoAtualizado.getDescricao()))
                    .andExpect(jsonPath("$.numero").value(enderecoAtualizado.getNumero()))
                    .andExpect(jsonPath("$.deletado").value(enderecoAtualizado.isDeletado()));
        }

        @Test
        @DisplayName("Deve retornar NOT_FOUND quando o endereço não for encontrado ao atualizar")
        void atualizarEnderecoNotFound() throws Exception {
            EnderecoDTO novoDTO = new EnderecoDTO(1L, "54321-000", "Nova Cidade", "Novo Endereco", 200, null, false);
            when(enderecoService.atualizar(eq(1L), any(Endereco.class)))
                    .thenThrow(new RegistroNotFoundException("Endereço"));

            String jsonContent = objectMapper.writeValueAsString(novoDTO);

            mockMvc.perform(put("/endereco/atualizar/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonContent))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Endereço não encontrado com este ID!"));
        }
    }

    @Nested
    @DisplayName("DELETE /endereco/deletar/{id}")
    class ExcluirEndpointTests {

        @Test
        @DisplayName("Deve excluir o endereço com sucesso")
        void excluirEnderecoSucesso() throws Exception {
            Endereco enderecoExcluido = new Endereco();
            enderecoExcluido.setId(1L);
            enderecoExcluido.setCep("12345-678");
            enderecoExcluido.setCidade("Cidade Teste");
            enderecoExcluido.setDescricao("Endereco Teste");
            enderecoExcluido.setNumero(100);
            enderecoExcluido.setDeletado(true);
            enderecoExcluido.setCliente(null);

            when(enderecoService.remover(1L)).thenReturn(enderecoExcluido);

            mockMvc.perform(delete("/endereco/deletar/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.deletado").value(true));
        }

        @Test
        @DisplayName("Deve retornar NOT_FOUND quando o endereço não for encontrado ao deletar")
        void excluirEnderecoNotFound() throws Exception {
            when(enderecoService.remover(1L))
                    .thenThrow(new RegistroNotFoundException("Endereço"));

            mockMvc.perform(delete("/endereco/deletar/{id}", 1L))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Endereço não encontrado com este ID!"));
        }

    }

    @Nested
    @DisplayName("GET /endereco/{id}")
    class EncontrarEndpointTests {

        @Test
        @DisplayName("Deve encontrar o endereço com sucesso")
        void encontrarEnderecoSucesso() throws Exception {
            when(enderecoService.buscarEnderecoPorId(1L)).thenReturn(endereco);

            mockMvc.perform(get("/endereco/{id}", 1L))
                    .andExpect(status().isFound())
                    .andExpect(jsonPath("$.id").value(endereco.getId()))
                    .andExpect(jsonPath("$.cep").value(endereco.getCep()))
                    .andExpect(jsonPath("$.cidade").value(endereco.getCidade()))
                    .andExpect(jsonPath("$.descricao").value(endereco.getDescricao()))
                    .andExpect(jsonPath("$.numero").value(endereco.getNumero()))
                    .andExpect(jsonPath("$.deletado").value(endereco.isDeletado()));
        }

        @Test
        @DisplayName("Deve retornar NOT_FOUND quando o endereço não for encontrado")
        void encontrarEnderecoNotFound() throws Exception {
            when(enderecoService.buscarEnderecoPorId(1L))
                    .thenThrow(new RegistroNotFoundException("Endereço"));

            mockMvc.perform(get("/endereco/{id}", 1L))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Endereço não encontrado com este ID!"));
        }
    }

    @Nested
    @DisplayName("GET /endereco/encontrarPeloCliente")
    class EncontrarPeloClienteEndpointTests {

        @Test
        @DisplayName("Deve retornar a lista de endereços do cliente com sucesso")
        void encontrarPeloClienteSucesso() throws Exception {
            List<Endereco> lista = List.of(endereco);
            when(enderecoService.findByClient(1L)).thenReturn(lista);

            mockMvc.perform(get("/endereco/encontrarPeloCliente")
                            .param("codigoCliente", "1"))
                    .andExpect(status().isFound())
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

            mockMvc.perform(get("/endereco/encontrarPeloCliente")
                            .param("codigoCliente", "1"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Cliente não encontrado com este ID!"));
        }
    }
}
