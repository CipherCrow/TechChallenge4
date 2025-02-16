package br.com.techchallenge.safedeliver.gerenciamentoclientes.controller;

import br.com.techchallenge.safedeliver.gerenciamentoclientes.domain.model.entities.Cliente;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.dto.ClienteDTO;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.exception.RegistroNotFoundException;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.mapper.ClienteMapper;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.service.ClienteService;
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

class ClienteControllerTest {

    @Mock
    private ClienteService clienteService;

    private MockMvc mockMvc;

    private AutoCloseable openMocks;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Cliente cliente;
    private ClienteDTO clienteDTO;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        ClienteController clienteController = new ClienteController(clienteService);
        mockMvc = MockMvcBuilders.standaloneSetup(clienteController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        cliente = new Cliente(1L, "Nome", "12345678901", "email@example.com", "123456789", 30, false);
        clienteDTO = ClienteMapper.toDTO(cliente);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    @DisplayName("POST /cliente/criar")
    class CriarEndpointTests {

        @Test
        @DisplayName("Deve criar o cliente com sucesso")
        void criarClienteSucesso() throws Exception {
            when(clienteService.criar(any(Cliente.class))).thenReturn(cliente);
            String jsonContent = objectMapper.writeValueAsString(clienteDTO);

            mockMvc.perform(post("/cliente/criar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonContent))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(cliente.getId()))
                    .andExpect(jsonPath("$.nome").value(cliente.getNome()))
                    .andExpect(jsonPath("$.cpf").value(cliente.getCpf()))
                    .andExpect(jsonPath("$.email").value(cliente.getEmail()))
                    .andExpect(jsonPath("$.telefone").value(cliente.getTelefone()))
                    .andExpect(jsonPath("$.idade").value(cliente.getIdade()))
                    .andExpect(jsonPath("$.excluido").value(cliente.isDeletado()));
        }
    }

    @Nested
    @DisplayName("PUT /cliente/atualizar/{id}")
    class AtualizarEndpointTests {

        @Test
        @DisplayName("Deve atualizar o cliente com sucesso")
        void atualizarClienteSucesso() throws Exception {
            Cliente clienteAtualizado = new Cliente(1L, "Novo Nome", "11122233344", "novoemail@example.com", "987654321", 35, false);
            ClienteDTO novoDTO = ClienteMapper.toDTO(clienteAtualizado);

            when(clienteService.atualizar(any(Cliente.class), eq(1L))).thenReturn(clienteAtualizado);
            String jsonContent = objectMapper.writeValueAsString(novoDTO);

            mockMvc.perform(put("/cliente/atualizar/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonContent))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(clienteAtualizado.getId()))
                    .andExpect(jsonPath("$.nome").value(clienteAtualizado.getNome()))
                    .andExpect(jsonPath("$.cpf").value(clienteAtualizado.getCpf()))
                    .andExpect(jsonPath("$.email").value(clienteAtualizado.getEmail()))
                    .andExpect(jsonPath("$.telefone").value(clienteAtualizado.getTelefone()))
                    .andExpect(jsonPath("$.idade").value(clienteAtualizado.getIdade()));
        }

        @Test
        @DisplayName("Deve retornar NOT_FOUND se cliente não for encontrado")
        void atualizarClienteNotFound() throws Exception {
            ClienteDTO novoDTO = ClienteMapper.toDTO(
                    new Cliente(1L,
                            "Novo Nome",
                            "11122233344",
                            "novoemail@example.com",
                            "987654321",
                            35,
                            false)
            );

            when(clienteService.atualizar(any(Cliente.class), eq(1L)))
                    .thenThrow(new RegistroNotFoundException("Cliente"));
            String jsonContent = objectMapper.writeValueAsString(novoDTO);

            mockMvc.perform(put("/cliente/atualizar/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonContent))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Cliente não encontrado com este ID!"));
        }
    }

    @Nested
    @DisplayName("DELETE /cliente/deletar/{id}")
    class ExcluirEndpointTests {

        @Test
        @DisplayName("Deve excluir o cliente com sucesso")
        void excluirClienteSucesso() throws Exception {
            cliente.setId(1L);
            cliente.setDeletado(true);

            when(clienteService.excluir(1L)).thenReturn(cliente);

            mockMvc.perform(delete("/cliente/deletar/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.excluido").value(true));
        }

        @Test
        @DisplayName("Deve retornar NOT_FOUND quando não achar quem excluir")
        void excluirClienteNotFoundException() throws Exception {
            when(clienteService.excluir(1L)).thenThrow(new RegistroNotFoundException("Cliente"));

            mockMvc.perform(delete("/cliente/deletar/{id}", 1L))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Cliente não encontrado com este ID!"));
        }
    }

    @Nested
    @DisplayName("GET /cliente/{id}")
    class EncontrarEndpointTests {

        @Test
        @DisplayName("Deve encontrar o cliente com sucesso")
        void encontrarClienteSucesso() throws Exception {
            when(clienteService.encontrarPeloID(1L)).thenReturn(cliente);

            mockMvc.perform(get("/cliente/{id}", 1L))
                    .andExpect(status().isFound())
                    .andExpect(jsonPath("$.id").value(cliente.getId()))
                    .andExpect(jsonPath("$.nome").value(cliente.getNome()))
                    .andExpect(jsonPath("$.cpf").value(cliente.getCpf()))
                    .andExpect(jsonPath("$.email").value(cliente.getEmail()))
                    .andExpect(jsonPath("$.telefone").value(cliente.getTelefone()))
                    .andExpect(jsonPath("$.idade").value(cliente.getIdade()))
                    .andExpect(jsonPath("$.excluido").value(cliente.isDeletado()));
        }

        @Test
        @DisplayName("Deve retornar NOT_FOUND se cliente não for encontrado")
        void encontrarClienteNotFound() throws Exception {
            when(clienteService.encontrarPeloID(1L)).thenThrow(new RegistroNotFoundException("Cliente"));

            mockMvc.perform(get("/cliente/{id}", 1L))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Cliente não encontrado com este ID!"));
        }
    }

    @Nested
    @DisplayName("GET /cliente/todos")
    class EncontrarTodosEndpointTests {

        @Test
        @DisplayName("Deve retornar todos os clientes")
        void encontrarTodosClientes() throws Exception {
            Cliente outroCliente = new Cliente(2L, "Outro Nome", "22233344455", "outro@example.com", "987654321", 40, false);
            List<Cliente> clientes = List.of(cliente, outroCliente);
            when(clienteService.listarTodos()).thenReturn(clientes);

            mockMvc.perform(get("/cliente/todos"))
                    .andExpect(status().isFound())
                    .andExpect(jsonPath("$[0].id").value(cliente.getId()))
                    .andExpect(jsonPath("$[1].id").value(outroCliente.getId()));
        }
    }
}
