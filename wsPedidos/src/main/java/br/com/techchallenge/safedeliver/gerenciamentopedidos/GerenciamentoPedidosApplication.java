package br.com.techchallenge.safedeliver.gerenciamentopedidos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GerenciamentoPedidosApplication {

	public static void main(String[] args) {
		SpringApplication.run(GerenciamentoPedidosApplication.class, args);
	}

}
