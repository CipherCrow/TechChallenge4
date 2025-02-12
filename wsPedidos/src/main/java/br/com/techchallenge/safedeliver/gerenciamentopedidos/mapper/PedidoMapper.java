package br.com.techchallenge.safedeliver.gerenciamentopedidos.mapper;

import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.Pedido;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.dto.PedidoDTO;

public class PedidoMapper {
    private PedidoMapper(){
        throw new IllegalStateException("Classe de utilidade");
    }

    public static PedidoDTO toDTO(Pedido pedido) {
        return new PedidoDTO(
            pedido.getId(),
            pedido.getCliente(),
            pedido.getItens(),
            pedido.getValorTotal(),
            pedido.getEndereco(),
            pedido.getStatusPedido()
        );
    }

    public static Pedido toEntity(PedidoDTO dto) {
        return Pedido.builder()
                .cliente(dto.cliente())
                .itens(dto.itens())
                .valorTotal(dto.valorTotal())
                .endereco(dto.endereco())
                .statusPedido(dto.status())
                .build();
    }
}
