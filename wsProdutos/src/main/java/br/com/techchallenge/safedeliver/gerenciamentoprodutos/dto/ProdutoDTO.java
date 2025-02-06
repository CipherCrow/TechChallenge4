package br.com.techchallenge.safedeliver.gerenciamentoprodutos.dto;

public record ProdutoDTO(
    Long id,
    String descricao,
    int estoque,
    Double preco,
    boolean deletado
){}
