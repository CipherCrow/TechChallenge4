package br.com.techchallenge.safedeliver.gerenciamentopedidos.service;

import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.ItemPedido;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.Produto;

import java.util.List;

public interface PedidoItemService {

    ItemPedido criar(Long codigoPedido, Long codigoItem, Integer quantidade);
    //boolean removerItem(Long codigoPedido, Long codigoItem);
    //ItemPedido findById(Long codigoPedido);
    //List<ItemPedido> listarTodosDoPedido(Long codigoPedido);
    Produto encontrarValidarProduto(Long codProduto, Integer quantidade);
}
