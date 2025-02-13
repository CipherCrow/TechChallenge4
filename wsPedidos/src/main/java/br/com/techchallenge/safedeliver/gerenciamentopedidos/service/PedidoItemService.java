package br.com.techchallenge.safedeliver.gerenciamentopedidos.service;

import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.ItemPedido;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.Produto;

public interface PedidoItemService {

    ItemPedido criar(Long codigoPedido, Long codigoItem, Integer quantidade);
    //boolean removerItem(Long codigoPedido, Long codigoItem);
    //ItemPedido findById(Long codigoPedido);
    //List<ItemPedido> validarItemsDoPedido(Long codigoPedido);
    Produto encontrarValidarProduto(Long codProduto, Integer quantidade);
}
