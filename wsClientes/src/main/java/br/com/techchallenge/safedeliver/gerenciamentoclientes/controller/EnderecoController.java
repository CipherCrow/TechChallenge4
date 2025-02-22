package br.com.techchallenge.safedeliver.gerenciamentoclientes.controller;

import br.com.techchallenge.safedeliver.gerenciamentoclientes.domain.model.entities.Endereco;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.dto.EnderecoDTO;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.mapper.EnderecoMapper;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.service.EnderecoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enderecos")
@RequiredArgsConstructor
public class EnderecoController {

    private final EnderecoService enderecoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EnderecoDTO criar(@Valid @RequestBody EnderecoDTO endereco, @RequestParam Long clienteId) {
        Endereco novoEndereco = enderecoService.adicionar(clienteId, EnderecoMapper.toEntity(endereco));
        return EnderecoMapper.toDTO(novoEndereco);
    }

    @PutMapping("/{enderecoId}")
    public ResponseEntity<EnderecoDTO> atualizar(@Valid @RequestBody EnderecoDTO endereco, @PathVariable Long enderecoId) {
        Endereco enderecoAtualizado = enderecoService.atualizar(enderecoId, EnderecoMapper.toEntity(endereco));
        return ResponseEntity.ok(EnderecoMapper.toDTO(enderecoAtualizado));
    }

    @DeleteMapping("/{enderecoId}")
    public ResponseEntity<Void> excluir(@PathVariable Long enderecoId) {
        enderecoService.remover(enderecoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{enderecoId}")
    public ResponseEntity<EnderecoDTO> encontrar(@PathVariable Long enderecoId) {
        Endereco enderecoEncontrado = enderecoService.buscarEnderecoPorId(enderecoId);
        return ResponseEntity.ok(EnderecoMapper.toDTO(enderecoEncontrado));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<EnderecoDTO>> encontrarPorCliente(@PathVariable Long clienteId) {
        List<Endereco> enderecos = enderecoService.findByClient(clienteId);
        return ResponseEntity.ok(enderecos.stream().map(EnderecoMapper::toDTO).toList());
    }
}
