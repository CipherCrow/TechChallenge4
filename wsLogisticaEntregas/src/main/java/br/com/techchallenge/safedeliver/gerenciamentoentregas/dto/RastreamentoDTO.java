package br.com.techchallenge.safedeliver.gerenciamentoentregas.dto;

import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Endereco;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Pedido;

public record RastreamentoDTO(
    Long id,
    Pedido pedido,
    Endereco endereco
){}
