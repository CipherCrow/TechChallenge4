package br.com.techchallenge.safedeliver.gerenciamentoentregas.service;

import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Localizacao;

import java.util.List;

public interface LocalizacaoService {
    Localizacao adicionar(Localizacao localizacao, Long idRastreamento);
    List<Localizacao> buscarPeloRastreamento(Long idRastreamento);
}
