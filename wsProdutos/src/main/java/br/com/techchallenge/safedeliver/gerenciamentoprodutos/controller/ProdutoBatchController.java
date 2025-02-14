package br.com.techchallenge.safedeliver.gerenciamentoprodutos.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Controller
@RequestMapping("/produto")
@RequiredArgsConstructor
public class ProdutoBatchController {

    private final JobLauncher jobLauncher;
    private final Job importJob;

    @PostMapping("/subirCsv")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            File tempFile = File.createTempFile("produtos-", ".csv");
            file.transferTo(tempFile);

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("localDoArquivo", tempFile.getAbsolutePath())
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(importJob, jobParameters);

            return ResponseEntity.ok("Job de importação iniciado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao executar o job: " + e.getMessage());
        }
    }
}