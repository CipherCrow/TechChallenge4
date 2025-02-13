package br.com.techchallenge.safedeliver.gerenciamentoentregas.dto;

import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Rastreamento;

import java.time.LocalDateTime;

public record LocalizacaoDTO(
    Long id,
    String longitude,
    String latitude,
    LocalDateTime horaRegistro,
    Rastreamento rastreamento
){}
