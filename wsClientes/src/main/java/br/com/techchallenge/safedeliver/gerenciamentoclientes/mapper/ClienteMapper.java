package br.com.techchallenge.safedeliver.gerenciamentoclientes.mapper;

import br.com.techchallenge.safedeliver.gerenciamentoclientes.domain.model.entities.Cliente;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.dto.ClienteDTO;

public class ClienteMapper {
    private ClienteMapper(){
        throw new IllegalStateException("Classe de utilidade");
    }

    public static ClienteDTO toDTO(Cliente cliente) {
        return new ClienteDTO(
            cliente.getId(),
            cliente.getNome(),
            cliente.getCpf(),
            cliente.getEmail(),
            cliente.getTelefone(),
            cliente.getIdade(),
            cliente.isDeletado()
        );
    }

    public static Cliente toEntity(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setId(dto.id());
        cliente.setNome(dto.nome());
        cliente.setCpf(dto.cpf());
        cliente.setEmail(dto.email());
        cliente.setTelefone(dto.telefone());
        cliente.setIdade(dto.idade());
        cliente.setDeletado(dto.excluido());

        return cliente;
    }
}
