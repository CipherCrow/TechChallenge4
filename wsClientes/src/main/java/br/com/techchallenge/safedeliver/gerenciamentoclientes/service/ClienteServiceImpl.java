package br.com.techchallenge.safedeliver.gerenciamentoclientes.service;

import br.com.techchallenge.safedeliver.gerenciamentoclientes.domain.model.entities.Cliente;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.exception.RegistroNotFoundException;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService{

    private final ClienteRepository clienteRepository;
    private static String idNotNull = "ID não pode ser nulo";

    @Override
    public Cliente criar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Override
    public Cliente atualizar(Cliente cliente, Long codCliente) {
        Objects.requireNonNull(codCliente, idNotNull);

        Cliente clienteEncontrado = clienteRepository.findById(codCliente)
                .orElseThrow(() -> new RegistroNotFoundException("Cliente "));

        clienteEncontrado.setNome(cliente.getNome());
        clienteEncontrado.setEmail(cliente.getEmail());
        clienteEncontrado.setCpf(cliente.getCpf());
        clienteEncontrado.setTelefone(cliente.getTelefone());
        clienteEncontrado.setIdade(cliente.getIdade());

        return clienteRepository.save(clienteEncontrado);
    }

    @Override
    public Cliente excluir(Long codCliente) {
        Objects.requireNonNull(codCliente, idNotNull);

        Cliente clienteEncontrado = clienteRepository.findById(codCliente)
                .orElseThrow(() -> new RegistroNotFoundException("Cliente "));

        clienteEncontrado.setDeletado(true);
        return clienteRepository.save(clienteEncontrado);
    }

    @Override
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Override
    public Cliente findById(Long codCliente) {
        Objects.requireNonNull(codCliente, idNotNull);

        return clienteRepository.findById(codCliente)
                .orElseThrow(() -> new RegistroNotFoundException("Cliente "));
    }
}
