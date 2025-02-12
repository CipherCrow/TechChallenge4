package br.com.techchallenge.safedeliver.gerenciamentoentregas.service;


import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Entregador;

import java.util.List;

public interface EntregadorService {

    Entregador criar(Entregador entregador);
    Entregador buscarPeloId(Long codEntregador);
    List<Entregador> buscarTodos();

}
