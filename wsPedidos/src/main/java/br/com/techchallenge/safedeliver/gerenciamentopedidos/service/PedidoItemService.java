package br.com.techchallenge.safedeliver.gerenciamentopedidos.service;

import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.ItemPedido;

import java.util.List;

public interface PedidoItemService {

    ItemPedido criar(Long codigoCliente);
    boolean removerItem(Long codigoPedido, Long codigoItem);
    ItemPedido findById(Long codigoPedido);
    List<ItemPedido> listarTodosDoPedido(Long codigoPedido);
}
