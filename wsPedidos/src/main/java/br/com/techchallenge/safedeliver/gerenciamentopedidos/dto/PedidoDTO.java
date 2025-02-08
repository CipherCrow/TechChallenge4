package br.com.techchallenge.safedeliver.gerenciamentopedidos.dto;

import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.ItemPedido;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.enums.StatusPedidoEnum;

import java.util.List;

public record PedidoDTO(
    Long id,
    Long cliente,
    List<ItemPedido> itens,
    Double valorTotal,
    Long endereco,
    StatusPedidoEnum status
){}
