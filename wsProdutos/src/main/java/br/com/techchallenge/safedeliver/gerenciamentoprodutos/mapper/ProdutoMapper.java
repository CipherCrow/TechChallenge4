package br.com.techchallenge.safedeliver.gerenciamentoprodutos.mapper;

import br.com.techchallenge.safedeliver.gerenciamentoprodutos.domain.model.entities.Produto;
import br.com.techchallenge.safedeliver.gerenciamentoprodutos.dto.ProdutoDTO;
import lombok.Builder;

public class ProdutoMapper {
    private ProdutoMapper(){
        throw new IllegalStateException("Classe de utilidade");
    }

    public static ProdutoDTO toDTO(Produto produto) {
        return new ProdutoDTO(
            produto.getId(),
            produto.getDescricao(),
            produto.getEstoque(),
            produto.getPreco(),
            produto.isDeletado()
        );
    }

    public static Produto toEntity(ProdutoDTO dto) {
       Produto produto = new Produto();
                produto.setId(dto.id());
                produto.setDescricao(dto.descricao());
                produto.setEstoque(dto.estoque());
                produto.setPreco(dto.preco());
                produto.setDeletado(dto.deletado());
        return produto;
    }
}
