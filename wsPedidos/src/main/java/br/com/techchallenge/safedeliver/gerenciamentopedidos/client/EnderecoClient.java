package br.com.techchallenge.safedeliver.gerenciamentopedidos.client;

import br.com.techchallenge.safedeliver.gerenciamentopedidos.dto.ClienteDTO;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.dto.EnderecoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "endereco-service", url = "http://localhost:8080/endereco")
public interface EnderecoClient {

    @GetMapping("/{id}")
    EnderecoDTO encontrarEndereco(@PathVariable Long id);
}
