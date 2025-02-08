package br.com.techchallenge.safedeliver.gerenciamentopedidos.service;

import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.Pedido;

import java.util.List;

public interface PedidoService {

    Pedido criar(Long codigoCliente);
    Pedido inserirItem(Long codigoPedido, Long codigoItem, int quantidade);
    Pedido removerItem(Long codigoPedido, Long codigoItem);
    Pedido findById(Long codigoPedido);
    List<Pedido> listarTodos();
    Pedido confirmarPedido(Long codigoPedido,  Long codigoEnderecoEntrega);
    boolean cancelarPedido(Long codigoPedido);
    Double obterValorTotalPedido(Long codigoPedido);
}
