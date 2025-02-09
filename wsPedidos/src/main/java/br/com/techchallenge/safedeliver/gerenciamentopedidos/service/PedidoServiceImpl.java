package br.com.techchallenge.safedeliver.gerenciamentopedidos.service;

import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.*;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.enums.StatusPedidoEnum;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.exception.RegistroNotFoundException;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.repository.PedidoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private PedidoRepository pedidoRepository;
    private PedidoItemService pedidoItemService;
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    private static String idNotNull = "ID não pode ser nulo";

    @Override
    public Pedido criar(Long clienteInformado) {
        Objects.requireNonNull(clienteInformado, idNotNull);
        Cliente cliente = encontrarCliente(clienteInformado);

        if(cliente == null){
            throw new RegistroNotFoundException("Cliente");
        }

        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .statusPedido(StatusPedidoEnum.EM_ANDAMENTO)
                .build();

        return pedidoRepository.save(pedido);
    }

    @Override
    public Pedido inserirItem(Long codigoPedido, Long codigoItem, int quantidade) {
        Objects.requireNonNull(codigoItem, idNotNull);
        Objects.requireNonNull(codigoPedido, idNotNull);

        Pedido pedidoEncontrado = encontrarPeloId(codigoPedido);
        ItemPedido itemPedido =  pedidoItemService.criar(codigoPedido, codigoItem, quantidade);
        pedidoEncontrado.getItens().add(itemPedido);

        return pedidoRepository.save(pedidoEncontrado);
    }

    @Override
    public Pedido removerItem(Long codigoPedido, Long codigoItemPedido) {
        Objects.requireNonNull(codigoItemPedido, idNotNull);
        Objects.requireNonNull(codigoPedido, idNotNull);

        Pedido pedidoEncontrado = encontrarPeloId(codigoPedido);
        ItemPedido itemPedido =
            pedidoEncontrado.getItens().stream().filter(
                item -> item.getId().equals(codigoItemPedido)
            ).findFirst().orElse(null);

        if(itemPedido == null){
            throw new RegistroNotFoundException("Item");
        }

        pedidoEncontrado.getItens().remove(itemPedido);

        return pedidoRepository.save(pedidoEncontrado);
    }

    @Override
    public Pedido encontrarPeloId(Long codigoPedido) {
        Objects.requireNonNull(codigoPedido, idNotNull);

        return pedidoRepository.findById(codigoPedido).orElseThrow(
                () -> new RegistroNotFoundException("Pedido"));
    }

    @Override
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    @Override
    public Pedido confirmarPedido(Long codigoPedido, Long codigoEnderecoEntrega) {
        Objects.requireNonNull(codigoEnderecoEntrega, idNotNull);

        Endereco endereco = encontrarEndereco(codigoEnderecoEntrega);
        if(endereco == null){
            throw new RegistroNotFoundException("Endereco");
        }

        Pedido pedido = encontrarPeloId(codigoPedido);
        if(pedido.getStatusPedido() != StatusPedidoEnum.EM_ANDAMENTO){
            throw new IllegalArgumentException("Apenas pedidos em andamento podem ser confirmados!");
        }



        pedido.setStatusPedido(StatusPedidoEnum.CONFIRMADO);
        pedido.setEndereco(endereco);
        return pedidoRepository.save(pedido);
    }

    @Override
    public Pedido cancelarPedido(Long codigoPedido) {
        Pedido pedido = encontrarPeloId(codigoPedido);
        if(pedido.getStatusPedido() != StatusPedidoEnum.EM_ANDAMENTO){
            throw new IllegalArgumentException("Apenas pedidos em andamento podem ser cancelados!");
        }
        pedido.setStatusPedido(StatusPedidoEnum.CANCELADO);
        return pedidoRepository.save(pedido);
    }

    @Override
    public Pedido atualizarValorTotal(Long codigoPedido) {
        Objects.requireNonNull(codigoPedido, idNotNull);
        Pedido pedido = encontrarPeloId(codigoPedido);

        Double valorTotal =
                pedido.getItens().stream()
                        .map(ItemPedido::getValorProduto)
                        .reduce(0.0,(Double vlrTotal, Double vlrAtual ) ->
                                vlrTotal + vlrAtual
                        );

        pedido.setValorTotal(valorTotal);
        return pedidoRepository.save(pedido);
    }

    @Override
    public Cliente encontrarCliente(Long codigoCliente) {
        Cliente cliente = null;

        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:8080/cliente/{id}"
                ,String.class
                ,codigoCliente
        );

        if(response.getStatusCode() == HttpStatus.NOT_FOUND){
            throw new RegistroNotFoundException("Cliente");
        }else{
            try{
                JsonNode clienteJson = objectMapper.readTree((response.getBody()));
                cliente = objectMapper.treeToValue(clienteJson.get("cliente"), Cliente.class);
            }catch(Exception e){
                // tratar depois
            }
        }

        return cliente;
    }

    @Override
    public Endereco encontrarEndereco(Long codigoEndereco) {

        Endereco enderecoEncontrado = null;

        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:8080/endereco/{id}"
            ,String.class
            ,codigoEndereco
        );

        if(response.getStatusCode() == HttpStatus.NOT_FOUND){
            throw new RegistroNotFoundException("Endereco");
        }else{
            try{
                JsonNode enderecoJson = objectMapper.readTree((response.getBody()));
                enderecoEncontrado = objectMapper.treeToValue(enderecoJson, Endereco.class);
            }catch(Exception e){
                // tratar depois
            }
        }


        //return true;
        return enderecoEncontrado;
    }

    @Override
    public Produto encontrarValidarProduto(Long codigoProduto) {

        for(ItemPedido itemPedido : itens ){
            Integer quantidade = itemPedido.getQuantidade();

            ResponseEntity<String> response = restTemplate.getForEntity(
                    "http://localhost:8080/api/produtos/{id}"
                    ,String.class
                    ,codigoProduto
            );

            if(response.getStatusCode() == HttpStatus.NOT_FOUND){
                throw new RegistroNotFoundException("Produto");
            }else{
                try{
                    JsonNode produtoJson = objectMapper.readTree((response.getBody()));
                    int quantidadeProduto = produtoJson.get("quantidade").asInt();
                }catch(Exception e){
                    // tratar depois
                }
            }
        }

        //return true;
        return null;
    }
}
