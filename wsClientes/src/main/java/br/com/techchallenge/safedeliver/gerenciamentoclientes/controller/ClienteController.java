package br.com.techchallenge.safedeliver.gerenciamentoclientes.controller;

import br.com.techchallenge.safedeliver.gerenciamentoclientes.domain.model.entities.Cliente;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.dto.ClienteDTO;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.mapper.ClienteMapper;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cliente")
@RequiredArgsConstructor
public class ClienteController {

    @Autowired
    private final ClienteService clienteService;

    @PostMapping("/criar")
    public ResponseEntity<ClienteDTO> criar(@RequestBody ClienteDTO cliente){
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ClienteMapper.toDTO(
                    clienteService.criar(ClienteMapper.toEntity(cliente))
            )
        );
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<ClienteDTO> atualizar(@Valid @RequestBody ClienteDTO cliente, @PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(
            ClienteMapper.toDTO(
                    clienteService.atualizar(ClienteMapper.toEntity(cliente),id)
            )
        );
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<ClienteDTO> excluir(@Valid @PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(
            ClienteMapper.toDTO(
                    clienteService.excluir(id)
            )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> encontrar(@Valid @PathVariable Long id){
        return ResponseEntity.status(HttpStatus.FOUND).body(
            ClienteMapper.toDTO(
                    clienteService.findById(id)
            )
        );
    }

    @GetMapping("/todos")
    public ResponseEntity<List<Cliente>> encontrarTodos(){
        return ResponseEntity.status(HttpStatus.FOUND).body(
            clienteService.listarTodos()
        );
    }
}
