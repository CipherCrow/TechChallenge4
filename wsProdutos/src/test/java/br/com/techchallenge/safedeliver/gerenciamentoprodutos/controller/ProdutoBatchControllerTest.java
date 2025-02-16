package br.com.techchallenge.safedeliver.gerenciamentoprodutos.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class ProdutoBatchControllerTest {

    @Mock
    private JobLauncher jobLauncher;
    @Mock
    private Job importJob;

    private MockMvc mockMvc;
    private AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        ProdutoBatchController controller = new ProdutoBatchController(jobLauncher, importJob);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    @DisplayName("POST /produto/subirCsv")
    class UploadFileTests {

        @Test
        @DisplayName("Deve iniciar o job com sucesso")
        void uploadFileSuccess() throws Exception {
            // Arrange: Cria um arquivo CSV simulado
            byte[] content = "descricao,estoque,preco\nProduto,10,100.0".getBytes();
            MockMultipartFile file = new MockMultipartFile("file", "produtos.csv", "text/csv", content);

            // Simula execução bem-sucedida do job (o retorno do run não é utilizado)
            when(jobLauncher.run(eq(importJob), any())).thenReturn(null);

            // Act & Assert
            mockMvc.perform(multipart("/produto/subirCsv")
                            .file(file))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Job de importação iniciado com sucesso!"));
        }

        @Test
        @DisplayName("Deve retornar erro ao executar o job")
        void uploadFileError() throws Exception {
            // Arrange: Cria um arquivo CSV simulado
            byte[] content = "descricao,estoque,preco\nProduto,10,100.0".getBytes();
            MockMultipartFile file = new MockMultipartFile("file", "produtos.csv", "text/csv", content);

            // Simula uma exceção na execução do job
            when(jobLauncher.run(eq(importJob), any()))
                    .thenThrow(new JobParametersInvalidException("Falha no job"));

            // Act & Assert
            mockMvc.perform(multipart("/produto/subirCsv")
                            .file(file))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string("Erro ao executar o job: Falha no job"));
        }
    }
}
