package br.com.techchallenge.safedeliver.gerenciamentoclientes.mapper;

import br.com.techchallenge.safedeliver.gerenciamentoclientes.domain.model.entities.Endereco;
import br.com.techchallenge.safedeliver.gerenciamentoclientes.dto.EnderecoDTO;

public class EnderecoMapper {
    private EnderecoMapper(){
        throw new IllegalStateException("Classe de utilidade");
    }

    public static EnderecoDTO toDTO(Endereco endereco) {
        return new EnderecoDTO(
            endereco.getId(),
                endereco.getCep(),
                endereco.getCidade(),
                endereco.getDescricao(),
                endereco.getNumero(),
                endereco.getCliente(),
                endereco.isDeletado()
        );
    }

    public static Endereco toEntity(EnderecoDTO dto) {
        return Endereco.builder()
                .id(dto.id())
                .cep(dto.cep())
                .cidade(dto.cidade())
                .descricao(dto.descricao())
                .numero(dto.numero())
                .cliente(dto.cliente())
                .deletado(dto.deletado())
                .build();
    }
}
