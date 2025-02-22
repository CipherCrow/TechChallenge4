package br.com.techchallenge.safedeliver.core.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/clientes")
    public ResponseEntity<String> clientesFallback() {
        return ResponseEntity.status(503).body("O serviço de clientes está temporariamente indisponível. Tente novamente mais tarde.");
    }

    @GetMapping("/pedidos")
    public ResponseEntity<String> pedidosFallback() {
        return ResponseEntity.status(503).body("O serviço de pedidos está temporariamente indisponível. Tente novamente mais tarde.");
    }

    @GetMapping("/produtos")
    public ResponseEntity<String> produtosFallback() {
        return ResponseEntity.status(503).body("O serviço de produtos está temporariamente indisponível. Tente novamente mais tarde.");
    }
}
