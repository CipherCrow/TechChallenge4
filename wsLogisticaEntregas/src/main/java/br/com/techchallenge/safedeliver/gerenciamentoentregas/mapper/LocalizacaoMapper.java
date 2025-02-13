package br.com.techchallenge.safedeliver.gerenciamentoentregas.mapper;

import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Localizacao;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.dto.LocalizacaoDTO;

public class LocalizacaoMapper {
    private LocalizacaoMapper(){
        throw new IllegalStateException("Classe de utilidade");
    }

    public static LocalizacaoDTO toDTO(Localizacao localizacao) {
        return new LocalizacaoDTO(
                localizacao.getId(),
                localizacao.getLatitude(),
                localizacao.getLongitude(),
                localizacao.getHoraRegistro(),
                localizacao.getRastreamento()
        );
    }

    public static Localizacao toEntity(LocalizacaoDTO dto) {
        return Localizacao.builder()
                .id(dto.id())
                .longitude(dto.longitude())
                .latitude(dto.latitude())
                .horaRegistro(dto.horaRegistro())
                .rastreamento(dto.rastreamento())
                .build();
    }
}
