package br.com.techchallenge.safedeliver.gerenciamentoentregas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GerenciamentoEntregasApplication {

	public static void main(String[] args) {
		SpringApplication.run(GerenciamentoEntregasApplication.class, args);
	}

}
