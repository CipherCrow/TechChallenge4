package br.com.techchallenge.safedeliver.gerenciamentoclientes.service;

import br.com.techchallenge.safedeliver.gerenciamentoclientes.domain.model.entities.Cliente;

import java.util.List;

public interface ClienteService {

    Cliente criar(Cliente cliente);
    Cliente atualizar(Cliente cliente, Long codCliente);
    Cliente excluir(Long codCliente);
    List<Cliente> listarTodos();
    Cliente encontrarPeloID(Long codCliente);
}
