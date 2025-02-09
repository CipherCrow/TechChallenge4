package br.com.techchallenge.safedeliver.gerenciamentopedidos.dto;

import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.Cliente;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.Endereco;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.ItemPedido;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.enums.StatusPedidoEnum;

import java.util.List;

public record PedidoDTO(
    Long id,
    Cliente cliente,
    List<ItemPedido> itens,
    Double valorTotal,
    Endereco endereco,
    StatusPedidoEnum status
){}
