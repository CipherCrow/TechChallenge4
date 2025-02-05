package br.com.techchallenge.safedeliver.gerenciamentoclientes.controller;

import br.com.techchallenge.safedeliver.gerenciamentoclientes.dto.EnderecoDTO;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.exception.RegistroNotFoundException;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.mapper.ClienteMapper;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.mapper.EnderecoMapper;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.service.EnderecoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/endereco")
@RequiredArgsConstructor
public class EnderecoController {

    @Autowired
    private final EnderecoService enderecoService;

    @PostMapping("/criar")
    public ResponseEntity<Object> criar(@RequestBody EnderecoDTO endereco,@RequestParam Long codigoCliente){
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    EnderecoMapper.toDTO(
                            enderecoService.adicionar(codigoCliente,EnderecoMapper.toEntity(endereco))
                    )
            );
        }catch(NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch(RegistroNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Object> atualizar(@RequestBody EnderecoDTO cliente, @PathVariable Long id){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(
                    EnderecoMapper.toDTO(
                            enderecoService.atualizar(id,EnderecoMapper.toEntity(cliente))
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
                    EnderecoMapper.toDTO(
                            enderecoService.remover(id)
                    )
            );
        }catch(NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch(RegistroNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/encontrar")
    public ResponseEntity<Object> encontrarPeloCliente(@RequestParam Long codigoCliente){
        try {
            return ResponseEntity.status(HttpStatus.FOUND).body(
                enderecoService.findByClient(codigoCliente)
            );
        }catch(NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch(RegistroNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
