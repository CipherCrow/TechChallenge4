package br.com.techchallenge.safedeliver.gerenciamentoentregas.mapper;

import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Rastreamento;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.dto.RastreamentoDTO;

public class RastreamentoMapper {
    private RastreamentoMapper(){
        throw new IllegalStateException("Classe de utilidade");
    }

    public static RastreamentoDTO toDTO(Rastreamento rastreamento) {
        return new RastreamentoDTO(
                rastreamento.getId(),
                rastreamento.getPedido(),
                rastreamento.getEndereco()
        );
    }

    public static Rastreamento toEntity(RastreamentoDTO dto) {
        return Rastreamento.builder()
                .id(dto.id())
                .pedido(dto.pedido())
                .endereco(dto.endereco())
                .build();
    }
}
