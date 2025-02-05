package br.com.techchallenge.safedeliver.gerenciamentoclientes.controller;

import br.com.techchallenge.safedeliver.gerenciamentoclientes.dto.ClienteDTO;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.exception.RegistroNotFoundException;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.mapper.ClienteMapper;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cliente")
@RequiredArgsConstructor
public class ClienteController {

    @Autowired
    private final ClienteService clienteService;

    @PostMapping("/criar")
    public ResponseEntity<Object> criar(@RequestBody ClienteDTO cliente){
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ClienteMapper.toDTO(
                            clienteService.criar(ClienteMapper.toEntity(cliente))
                    )
            );
        }catch(NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Object> atualizar(@RequestBody ClienteDTO cliente, @PathVariable Long id){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(
                    ClienteMapper.toDTO(
                            clienteService.atualizar(ClienteMapper.toEntity(cliente),id)
                    )
            );
        }catch(NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch(RegistroNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Object> excluir(@PathVariable Long id){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(
                    ClienteMapper.toDTO(
                            clienteService.excluir(id)
                    )
            );
        }catch(NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch(RegistroNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> encontrar(@PathVariable Long id){
        try {
            return ResponseEntity.status(HttpStatus.FOUND).body(
                    ClienteMapper.toDTO(
                            clienteService.findById(id)
                    )
            );
        }catch(NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch(RegistroNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/todos")
    public ResponseEntity<Object> encontrarTodos(){
        try {
            return ResponseEntity.status(HttpStatus.FOUND).body(
                            clienteService.listarTodos()
            );
        }catch(NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch(RegistroNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
