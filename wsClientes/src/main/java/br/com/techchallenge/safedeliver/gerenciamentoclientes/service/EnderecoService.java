package br.com.techchallenge.safedeliver.gerenciamentoclientes.service;

import br.com.techchallenge.safedeliver.gerenciamentoclientes.domain.model.entities.Endereco;

import java.util.List;

public interface EnderecoService {

    Endereco adicionar(Long codCliente, Endereco endereco);
    Endereco atualizar(Long codEndereco, Endereco endereco);
    List<Endereco> findByClient(Long codigoCliente);
    Endereco buscarEnderecoPorId(Long codEndereco);
    Endereco remover(Long codEndereco);
}
