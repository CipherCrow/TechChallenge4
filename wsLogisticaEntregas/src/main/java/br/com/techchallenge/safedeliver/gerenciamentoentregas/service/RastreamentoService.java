package br.com.techchallenge.safedeliver.gerenciamentoentregas.service;

import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Endereco;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Pedido;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Rastreamento;

import java.util.List;

public interface RastreamentoService {
    Rastreamento criarRastreio(Long codPedido,Long codEndereco);
    Rastreamento encontrarPeloId(Long codRastreamento);
    List<Rastreamento> encontrarAgruparPeloCep(String cep);
    Pedido encontrarPedido(Long codPedido);
    Endereco encontrarEndereco(Long codEndereco);
}
