package br.com.techchallenge.safedeliver.gerenciamentopedidos.controller;

import br.com.techchallenge.safedeliver.gerenciamentopedidos.exception.RegistroNotFoundException;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.mapper.PedidoMapper;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.service.PedidoItemService;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/pedido")
@RequiredArgsConstructor
public class PedidoController {

    @Autowired
    private final PedidoService pedidoService;

    @Autowired
    private final PedidoItemService pedidoItemService;

    @PostMapping("/criar")
    public ResponseEntity<Object> criar(@RequestParam Long cliente){
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    PedidoMapper.toDTO(
                            pedidoService.criar(cliente)
                    )
            );
        }catch(NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/inserirItem/{id}")
    public ResponseEntity<Object> inserirItem(@RequestParam Long idProduto,
                                              @RequestParam int quantidade,
                                              @PathVariable Long id){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(
                    PedidoMapper.toDTO(
                            pedidoService.inserirItem(id,idProduto,quantidade)
                    )
            );
        }catch(NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch(RegistroNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/removerItem/{id}")
    public ResponseEntity<Object> removerItem(@PathVariable Long id,
                                          @RequestParam Long codigoItem){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(
                    PedidoMapper.toDTO(
                            pedidoService.removerItem(id,codigoItem)
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
                    PedidoMapper.toDTO(
                            pedidoService.findById(id)
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
                    pedidoService.listarTodos()
            );
        }catch(NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch(RegistroNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/confirmarPedido/{id}")
    public ResponseEntity<Object> confirmarPedido(@PathVariable Long id,
                                                  @RequestParam Long endereco){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(
                    PedidoMapper.toDTO(
                            pedidoService.confirmarPedido(id,endereco)
                    )
            );
        }catch(NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch(RegistroNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/deletarPedido/{id}")
    public ResponseEntity<Object> deletarPedido(@PathVariable Long id){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(
                pedidoService.cancelarPedido(id)
            );
        }catch(NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch(RegistroNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
