package br.com.techchallenge.safedeliver.gerenciamentopedidos.service;

import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.Cliente;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.Endereco;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.Pedido;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.Produto;

import java.util.List;

public interface PedidoService {

    Pedido criar(Long codigoCliente);
    Pedido inserirItem(Long codigoPedido, Long codigoItem, int quantidade);
    Pedido removerItem(Long codigoPedido, Long codigoItem);
    Pedido encontrarPeloId(Long codigoPedido);
    List<Pedido> listarTodos();
    Pedido confirmarPedido(Long codigoPedido,  Long codigoEnderecoEntrega);
    Pedido cancelarPedido(Long codigoPedido);
    Pedido atualizarValorTotal(Long codigoPedido);
    Cliente encontrarCliente(Long codigoCliente);
    Endereco encontrarEndereco(Long codigoProduto);
    Produto encontrarValidarProduto(Long codigoProduto);

}
