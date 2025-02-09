package br.com.techchallenge.safedeliver.gerenciamentoprodutos.controller;

import br.com.techchallenge.safedeliver.gerenciamentoprodutos.dto.ProdutoDTO;
import br.com.techchallenge.safedeliver.gerenciamentoprodutos.exception.RegistroNotFoundException;
import br.com.techchallenge.safedeliver.gerenciamentoprodutos.mapper.ProdutoMapper;
import br.com.techchallenge.safedeliver.gerenciamentoprodutos.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/produto")
@RequiredArgsConstructor
public class ProdutoController {

    @Autowired
    private final ProdutoService produtoService;

    @PostMapping("/criar")
    public ResponseEntity<Object> criar(@RequestBody ProdutoDTO cliente){
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ProdutoMapper.toDTO(
                            produtoService.criar(ProdutoMapper.toEntity(cliente))
                    )
            );
        }catch(NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Object> atualizar(@RequestBody ProdutoDTO produto, @Valid @PathVariable Long id){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(
                    ProdutoMapper.toDTO(
                            produtoService.atualizar(ProdutoMapper.toEntity(produto),id)
                    )
            );
        }catch(NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch(RegistroNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/atualizarQuantidade/{id}")
    public ResponseEntity<Object> atualizarQuantidade(@PathVariable Long id, @RequestParam Integer quantidadeNova){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(
                    ProdutoMapper.toDTO(
                            produtoService.atualizarQuantidade(id,quantidadeNova)
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
                    ProdutoMapper.toDTO(
                            produtoService.excluir(id)
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
                    ProdutoMapper.toDTO(
                            produtoService.encontrarPeloId(id)
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
                            produtoService.findAll()
            );
        }catch(NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch(RegistroNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/todosValidos")
    public ResponseEntity<Object> encontrarTodosValidos(){
        try {
            return ResponseEntity.status(HttpStatus.FOUND).body(
                    produtoService.findAllValidos()
            );
        }catch(NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch(RegistroNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/validarReduzir/{id}/{qtd}")
    public ResponseEntity<Object> validaReduzEstoque(@PathVariable Long id,
                                                     @PathVariable Integer qtd){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(
                    ProdutoMapper.toDTO(
                            produtoService.validarReduzir(id,qtd)
                    )
            );
        }catch(NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch(RegistroNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        }
    }
}
