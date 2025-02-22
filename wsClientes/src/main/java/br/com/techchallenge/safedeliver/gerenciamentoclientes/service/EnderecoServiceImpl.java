package br.com.techchallenge.safedeliver.gerenciamentoclientes.service;

import br.com.techchallenge.safedeliver.gerenciamentoclientes.domain.model.entities.Cliente;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.domain.model.entities.Endereco;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.exception.RegistroNotFoundException;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.repository.EnderecoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnderecoServiceImpl implements EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final ClienteService clienteService;
    private static final String ID_NAO_PODE_SER_NULO = "ID não pode ser nulo";

    @Override
    public Endereco adicionar(Long codCliente, Endereco endereco) {
        if (codCliente == null) {
            throw new IllegalArgumentException(ID_NAO_PODE_SER_NULO);
        }

        Cliente clienteEncontrado = clienteService.encontrarPeloID(codCliente);
        endereco.setCliente(clienteEncontrado);

        return enderecoRepository.save(endereco);
    }

    @Override
    public Endereco atualizar(Long codEndereco, Endereco endereco) {
        if (codEndereco == null) {
            throw new IllegalArgumentException(ID_NAO_PODE_SER_NULO);
        }

        Endereco enderecoEncontrado = enderecoRepository.findById(codEndereco)
                .orElseThrow(() -> new RegistroNotFoundException("Endereço"));

        enderecoEncontrado.setCep(endereco.getCep());
        enderecoEncontrado.setCidade(endereco.getCidade());
        enderecoEncontrado.setDescricao(endereco.getDescricao());
        enderecoEncontrado.setNumero(endereco.getNumero());

        return enderecoRepository.save(enderecoEncontrado);
    }

    @Override
    public List<Endereco> findByClient(Long codigoCliente) {
        if (codigoCliente == null) {
            throw new IllegalArgumentException(ID_NAO_PODE_SER_NULO);
        }

        clienteService.encontrarPeloID(codigoCliente);
        return enderecoRepository.findEnderecoByCliente_Id(codigoCliente);
    }

    @Override
    public Endereco buscarEnderecoPorId(Long codEndereco) {
        if (codEndereco == null) {
            throw new IllegalArgumentException(ID_NAO_PODE_SER_NULO);
        }

        return enderecoRepository.findById(codEndereco)
                .orElseThrow(() -> new RegistroNotFoundException("Endereço"));
    }

    @Override
    public Endereco remover(Long codEndereco) {
        if (codEndereco == null) {
            throw new IllegalArgumentException(ID_NAO_PODE_SER_NULO);
        }

        Endereco enderecoEncontrado = buscarEnderecoPorId(codEndereco);
        enderecoRepository.delete(enderecoEncontrado);
        return enderecoEncontrado;
    }
}
