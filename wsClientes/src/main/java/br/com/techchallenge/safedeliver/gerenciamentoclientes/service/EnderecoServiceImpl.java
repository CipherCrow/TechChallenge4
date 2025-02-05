package br.com.techchallenge.safedeliver.gerenciamentoclientes.service;

import br.com.techchallenge.safedeliver.gerenciamentoclientes.domain.model.entities.Endereco;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnderecoServiceImpl implements EnderecoService{

    @Override
    public Endereco adicionar(Long codCliente, Endereco endereco) {
        return null;
    }

    @Override
    public Endereco atualizar(Long codEndereco, Endereco endereco) {
        return null;
    }

    @Override
    public List<Endereco> findByClient(Long codigoCliente) {
        return List.of();
    }

    @Override
    public Endereco remover(Long codEndereco) {
        return null;
    }
}
