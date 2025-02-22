package br.com.techchallenge.safedeliver.gerenciamentopedidos.controller;

import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.Pedido;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.dto.PedidoDTO;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.mapper.PedidoMapper;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.service.PedidoItemService;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.service.PedidoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@AllArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    private final PedidoItemService pedidoItemService;

    @PostMapping("/criar")
    public ResponseEntity<PedidoDTO> criar(@Valid @RequestParam Long cliente){
        return ResponseEntity.status(HttpStatus.CREATED).body(
            PedidoMapper.toDTO(
                pedidoService.criar(cliente)
            )
        );
    }

    @PutMapping("/inserirItem/{id}")
    public ResponseEntity<PedidoDTO> inserirItem(@Valid @RequestParam Long idProduto,
                                              @RequestParam int quantidade,
                                              @Valid @PathVariable Long id){
        pedidoService.inserirItem(id,idProduto,quantidade);
        return ResponseEntity.status(HttpStatus.OK).body(
            PedidoMapper.toDTO(
                pedidoService.atualizarValorTotal(id)
            )
        );
    }

    @DeleteMapping("/removerItem/{id}")
    public ResponseEntity<PedidoDTO> removerItem(@Valid @PathVariable Long id,
                                              @Valid @RequestParam Long codigoItem){
        pedidoService.removerItem(id,codigoItem);
        return ResponseEntity.status(HttpStatus.OK).body(
            PedidoMapper.toDTO(
                pedidoService.atualizarValorTotal(id)
            )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> encontrar(@Valid @PathVariable Long id){
        return ResponseEntity.status(HttpStatus.FOUND).body(
            PedidoMapper.toDTO(
                pedidoService.encontrarPeloId(id)
            )
        );
    }

    @GetMapping("/todos")
    public ResponseEntity<List<Pedido>>encontrarTodos(){
        return ResponseEntity.status(HttpStatus.FOUND).body(
            pedidoService.listarTodos()
        );
    }

    @PutMapping("/confirmarPedido/{id}")
    public ResponseEntity<PedidoDTO> confirmarPedido(@PathVariable Long id,
                                                  @Valid @RequestParam Long endereco){
        return ResponseEntity.status(HttpStatus.OK).body(
            PedidoMapper.toDTO(
                pedidoService.confirmarPedido(id,endereco)
            )
        );
    }

    @DeleteMapping("/deletarPedido/{id}")
    public ResponseEntity<PedidoDTO> deletarPedido(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(
            PedidoMapper.toDTO(
                pedidoService.cancelarPedido(id)
            )
        );
    }
}
