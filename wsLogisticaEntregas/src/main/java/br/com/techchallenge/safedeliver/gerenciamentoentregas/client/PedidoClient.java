package br.com.techchallenge.safedeliver.gerenciamentoentregas.client;

import br.com.techchallenge.safedeliver.gerenciamentoentregas.dto.PedidoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "pedido-service", url = "http://localhost:8080/pedido")
public interface PedidoClient {

    @GetMapping("/{id}")
    PedidoDTO pegarPedido(@PathVariable Long id);
}
