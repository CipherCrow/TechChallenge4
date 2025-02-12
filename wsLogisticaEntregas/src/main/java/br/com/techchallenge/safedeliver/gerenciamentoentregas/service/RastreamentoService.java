package br.com.techchallenge.safedeliver.gerenciamentoentregas.service;

import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Localizacao;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Rastreamento;

import java.util.List;

public interface RastreamentoService {
    Rastreamento criarRastreio(Long codPedido,Long codEndereco, Long codEntregador);
    List<Rastreamento> agruparPeloCep(String cep);
}
