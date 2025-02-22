package br.com.techchallenge.safedeliver.gerenciamentoclientes.controller;

import br.com.techchallenge.safedeliver.gerenciamentoclientes.domain.model.entities.Cliente;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.dto.ClienteDTO;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.mapper.ClienteMapper;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteDTO> criar(@Valid @RequestBody ClienteDTO cliente) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ClienteMapper.toDTO(clienteService.criar(ClienteMapper.toEntity(cliente)))
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> atualizar(@Valid @RequestBody ClienteDTO cliente, @PathVariable Long id) {
        return ResponseEntity.ok(
                ClienteMapper.toDTO(clienteService.atualizar(ClienteMapper.toEntity(cliente), id))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        clienteService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> encontrar(@PathVariable Long id) {
        return ResponseEntity.ok(ClienteMapper.toDTO(clienteService.encontrarPeloID(id)));
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> encontrarTodos() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }
}
