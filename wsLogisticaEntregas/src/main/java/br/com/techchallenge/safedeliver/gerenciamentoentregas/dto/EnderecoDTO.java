package br.com.techchallenge.safedeliver.gerenciamentoentregas.dto;


import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Cliente;

public record EnderecoDTO(
    Long id,
    String cep,
    String cidade,
    String descricao,
    int numero,
    Cliente cliente,
    boolean deletado
){}
