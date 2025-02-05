package br.com.techchallenge.safedeliver.gerenciamentoclientes.service;

import br.com.techchallenge.safedeliver.gerenciamentoclientes.domain.model.entities.Cliente;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteServiceImpl implements ClienteService{

    @Override
    public Cliente criar(Cliente cliente) {
        return null;
    }

    @Override
    public Cliente atualizar(Cliente cliente, Long codCliente) {
        return null;
    }

    @Override
    public Cliente excluir(Long codCliente) {
        return null;
    }

    @Override
    public List<Cliente> listarTodos() {
        return List.of();
    }

    @Override
    public Cliente findById(Long codCliente) {
        return null;
    }
}
