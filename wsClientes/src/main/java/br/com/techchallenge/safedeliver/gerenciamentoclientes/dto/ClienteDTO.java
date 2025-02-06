package br.com.techchallenge.safedeliver.gerenciamentoclientes.dto;

public record ClienteDTO(
    Long id,
    String nome,
    String cpf,
    String email,
    String telefone,
    int idade,
    boolean excluido
){}
