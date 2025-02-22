package br.com.techchallenge.safedeliver.gerenciamentoprodutos.controller;

import br.com.techchallenge.safedeliver.gerenciamentoprodutos.domain.model.entities.Produto;
import br.com.techchallenge.safedeliver.gerenciamentoprodutos.dto.ProdutoDTO;
import br.com.techchallenge.safedeliver.gerenciamentoprodutos.mapper.ProdutoMapper;
import br.com.techchallenge.safedeliver.gerenciamentoprodutos.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    @Autowired
    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping("/criar")
    public ResponseEntity<ProdutoDTO> criar(@RequestBody ProdutoDTO cliente){
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ProdutoMapper.toDTO(
                    produtoService.criar(ProdutoMapper.toEntity(cliente))
            )
        );
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<ProdutoDTO> atualizar(@RequestBody ProdutoDTO produto, @Valid @PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(
            ProdutoMapper.toDTO(
                    produtoService.atualizar(ProdutoMapper.toEntity(produto),id)
            )
        );
    }

    @PutMapping("/atualizarQuantidade/{id}")
    public ResponseEntity<ProdutoDTO> atualizarQuantidade(@PathVariable Long id, @RequestParam Integer quantidadeNova){
        return ResponseEntity.status(HttpStatus.OK).body(
            ProdutoMapper.toDTO(
                    produtoService.atualizarQuantidade(id,quantidadeNova)
            )
        );
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<ProdutoDTO> excluir(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(
            ProdutoMapper.toDTO(
                    produtoService.excluir(id)
            )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDTO> encontrar(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.FOUND).body(
                ProdutoMapper.toDTO(
                        produtoService.encontrarPeloId(id)
                )
        );
    }

    @GetMapping("/todos")
    public ResponseEntity<List<Produto>> encontrarTodos(){
        return ResponseEntity.status(HttpStatus.FOUND).body(
            produtoService.findAll()
        );
    }

    @GetMapping("/todosValidos")
    public ResponseEntity<List<Produto>> encontrarTodosValidos(){
        return ResponseEntity.status(HttpStatus.FOUND).body(
            produtoService.findAllValidos()
        );
    }

    @GetMapping("/validarEstoque/{id}")
    public ResponseEntity<ProdutoDTO> validarEstoque(@PathVariable Long id,
                                                           @RequestParam Integer qtd){
        return ResponseEntity.status(HttpStatus.OK).body(
                ProdutoMapper.toDTO(
                        produtoService.validarEstoque(id,qtd)
                )
        );
    }

    @PostMapping("/validarReduzirEstoque/{id}")
    public ResponseEntity<ProdutoDTO> validaReduzirEstoque(@PathVariable Long id,
                                                           @RequestParam Integer qtd){
        return ResponseEntity.status(HttpStatus.OK).body(
            ProdutoMapper.toDTO(
                produtoService.validarReduzirEstoque(id,qtd)
            )
        );
    }
}
