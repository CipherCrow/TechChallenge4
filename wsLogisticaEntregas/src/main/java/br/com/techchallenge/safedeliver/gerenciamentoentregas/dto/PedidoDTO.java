package br.com.techchallenge.safedeliver.gerenciamentoentregas.dto;

import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Cliente;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Endereco;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.ItemPedido;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.enums.StatusPedidoEnum;

import java.util.List;

public record PedidoDTO(
    Long id,
    Cliente cliente,
    List<ItemPedido> itens,
    Double valorTotal,
    Endereco endereco,
    StatusPedidoEnum status
){}
