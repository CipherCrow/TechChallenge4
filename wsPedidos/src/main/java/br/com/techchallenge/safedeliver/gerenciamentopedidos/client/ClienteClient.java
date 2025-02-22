package br.com.techchallenge.safedeliver.gerenciamentopedidos.client;

import br.com.techchallenge.safedeliver.gerenciamentopedidos.dto.ClienteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cliente-service", url = "http://core:8080/clientes")
public interface ClienteClient {

    @GetMapping("/{id}")
    ClienteDTO encontrarCliente(@PathVariable Long id);
}
