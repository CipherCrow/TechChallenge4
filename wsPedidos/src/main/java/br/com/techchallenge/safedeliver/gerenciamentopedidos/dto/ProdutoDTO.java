package br.com.techchallenge.safedeliver.gerenciamentopedidos.dto;

public record ProdutoDTO(
    Long id,
    String descricao,
    int estoque,
    Double preco,
    boolean deletado
){}
