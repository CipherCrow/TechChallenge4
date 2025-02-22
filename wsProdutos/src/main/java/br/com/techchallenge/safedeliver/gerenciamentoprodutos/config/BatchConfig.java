package br.com.techchallenge.safedeliver.gerenciamentoprodutos.config;

import br.com.techchallenge.safedeliver.gerenciamentoprodutos.domain.model.entities.Produto;
import br.com.techchallenge.safedeliver.gerenciamentoprodutos.repository.ProdutoRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Bean
    public Job importProdutosJob(JobRepository jobRepository, Step importProdutosStep) {
        return new JobBuilder("importProdutosJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(importProdutosStep)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Produto> produtoReader(
            @Value("#{jobParameters['localDoArquivo']}") String caminhoDoArquivo) {
        FlatFileItemReader<Produto> reader = new FlatFileItemReader<>();

        if (caminhoDoArquivo != null && !caminhoDoArquivo.isEmpty()) {
            reader.setResource(new FileSystemResource(caminhoDoArquivo));
        } else {
            reader.setResource(new ClassPathResource("produtos.csv"));
        }
        reader.setLinesToSkip(1); /*lembrar de colocar um header no arquivo*/

        DefaultLineMapper<Produto> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setNames("descricao", "estoque", "preco");
        lineMapper.setLineTokenizer(tokenizer);

        BeanWrapperFieldSetMapper<Produto> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Produto.class);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        reader.setLineMapper(lineMapper);
        return reader;
    }

    @Bean
    public ItemProcessor<Produto, Produto> produtoProcessor() {
        return produto -> produto;
    }

    @Bean
    public ItemWriter<Produto> produtoWriter(ProdutoRepository produtoRepository) {
        return produtoRepository::saveAll;
    }

    @Bean
    public Step importProdutosStep(JobRepository jobRepository,
                                   PlatformTransactionManager transactionManager,
                                   FlatFileItemReader<Produto> produtoReader,
                                   ItemProcessor<Produto, Produto> produtoProcessor,
                                   ItemWriter<Produto> produtoWriter) {
        return new StepBuilder("importProdutosStep", jobRepository)
                .<Produto, Produto>chunk(15, transactionManager) /*Para fazer de 15 em 150*/
                .reader(produtoReader)
                .processor(produtoProcessor)
                .writer(produtoWriter)
                .build();
    }
}