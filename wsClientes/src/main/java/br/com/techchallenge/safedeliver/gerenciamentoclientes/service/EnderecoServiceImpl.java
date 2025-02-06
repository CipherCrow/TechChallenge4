package br.com.techchallenge.safedeliver.gerenciamentoclientes.service;

import br.com.techchallenge.safedeliver.gerenciamentoclientes.domain.model.entities.Cliente;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.domain.model.entities.Endereco;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.exception.RegistroNotFoundException;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.repository.EnderecoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EnderecoServiceImpl implements EnderecoService{

    private final EnderecoRepository enderecoRepository;
    private final ClienteService clienteService;
    private static String idNotNull = "ID não pode ser nulo";

    @Override
    public Endereco adicionar(Long codCliente, Endereco endereco) {
        Cliente clienteEncontrado = clienteService.findById(codCliente);
        endereco.setCliente(clienteEncontrado);

        return enderecoRepository.save(endereco);
    }

    /***
     * Decidido pela regra de negocio que não é possível alterar o cliente do endereço,
     * sendo necessário realizar um novo cadastro
     * ***/
    @Override
    public Endereco atualizar(Long codEndereco, Endereco endereco) {
        Objects.requireNonNull(codEndereco, idNotNull);

        Endereco enderecoEncontrado = enderecoRepository.findById(codEndereco)
                .orElseThrow(() -> new RegistroNotFoundException("Endereço "));

        enderecoEncontrado.setCep(endereco.getCep());
        enderecoEncontrado.setCidade(endereco.getCidade());
        enderecoEncontrado.setDescricao(endereco.getDescricao());
        enderecoEncontrado.setNumero(endereco.getNumero());

        return enderecoRepository.save(endereco);
    }

    @Override
    public List<Endereco> findByClient(Long codigoCliente) {
        Objects.requireNonNull(codigoCliente, idNotNull);

        Cliente clienteEncontrado = clienteService.findById(codigoCliente);

        return enderecoRepository.findEnderecoByCliente_Id(clienteEncontrado.getId());
    }

    @Override
    public Endereco remover(Long codEndereco) {
        Objects.requireNonNull(codEndereco, idNotNull);

        Endereco enderecoEncontrado = enderecoRepository.findById(codEndereco)
                .orElseThrow(() -> new RegistroNotFoundException("Endereço "));

        enderecoEncontrado.setDeletado(true);
        return enderecoRepository.save(enderecoEncontrado);
    }
}
