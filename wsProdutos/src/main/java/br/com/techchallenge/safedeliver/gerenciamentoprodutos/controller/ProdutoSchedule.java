package br.com.techchallenge.safedeliver.gerenciamentoprodutos.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProdutoSchedule {

    private final JobLauncher jobLauncher;
    private final Job importProdutosJob;

    /*A CADA 2 MINUTOS*/
    @Scheduled(cron = "0 */2 * * * ?")
    public void runScheduledJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(importProdutosJob, jobParameters);
        } catch (Exception e) {
            System.err.println("Erro ao executar o job agendado: " + e.getMessage());
        }
    }
}
