package br.com.techchallenge.safedeliver.gerenciamentoclientes.controller;

import br.com.techchallenge.safedeliver.gerenciamentoclientes.domain.model.entities.Endereco;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.dto.EnderecoDTO;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.mapper.EnderecoMapper;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.service.EnderecoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/endereco")
@RequiredArgsConstructor
public class EnderecoController {

    @Autowired
    private final EnderecoService enderecoService;

    @PostMapping("/criar")
    public ResponseEntity<EnderecoDTO> criar(@RequestBody EnderecoDTO endereco,@RequestParam Long codigoCliente){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                EnderecoMapper.toDTO(
                        enderecoService.adicionar(codigoCliente,EnderecoMapper.toEntity(endereco))
                )
        );
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<EnderecoDTO> atualizar(@Valid @RequestBody EnderecoDTO cliente, @PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(
                EnderecoMapper.toDTO(
                        enderecoService.atualizar(id,EnderecoMapper.toEntity(cliente))
                )
        );
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<EnderecoDTO> excluir(@Valid @PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(
                EnderecoMapper.toDTO(
                        enderecoService.remover(id)
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnderecoDTO> encontrar(@Valid @PathVariable Long id){
        return ResponseEntity.status(HttpStatus.FOUND).body(
                EnderecoMapper.toDTO(
                        enderecoService.buscarEnderecoPorId(id)
                )
        );
    }

    @GetMapping("/encontrarPeloCliente")
    public ResponseEntity<List<Endereco>> encontrarPeloCliente(@Valid @RequestParam Long codigoCliente){
        return ResponseEntity.status(HttpStatus.FOUND).body(
            enderecoService.findByClient(codigoCliente)
        );
    }
}
