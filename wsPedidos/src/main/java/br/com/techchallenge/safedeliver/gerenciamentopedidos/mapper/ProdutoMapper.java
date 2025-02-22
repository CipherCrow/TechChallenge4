package br.com.techchallenge.safedeliver.gerenciamentopedidos.mapper;


import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.Produto;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.dto.ProdutoDTO;

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
        return Produto.builder()
                .id(dto.id())
                .descricao(dto.descricao())
                .estoque(dto.estoque())
                .preco(dto.preco())
                .deletado(dto.deletado())
                .build();
    }
}
