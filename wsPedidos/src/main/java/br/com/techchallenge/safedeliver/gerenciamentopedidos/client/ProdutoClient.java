package br.com.techchallenge.safedeliver.gerenciamentopedidos.client;

import br.com.techchallenge.safedeliver.gerenciamentopedidos.dto.ProdutoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "produto-service", url = "http://localhost:8080/produto")
public interface ProdutoClient {

    @GetMapping("/validarEstoque/{id}")
    ProdutoDTO validaEstoque(@PathVariable Long id,
                                    @RequestParam Integer quantidade);

    @PostMapping("/validarReduzirEstoque/{id}")
    ProdutoDTO validaReduzirEstoque(@PathVariable Long id,
                                    @RequestParam Integer quantidade);
}
