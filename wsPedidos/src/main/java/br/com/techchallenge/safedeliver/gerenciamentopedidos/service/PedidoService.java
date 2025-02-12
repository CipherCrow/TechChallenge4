package br.com.techchallenge.safedeliver.gerenciamentopedidos.service;

import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.*;

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
    boolean validarProdutosParaFechamento(List<ItemPedido> itens);
}
