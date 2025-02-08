package br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities;

import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.enums.StatusPedidoEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name = "tb_pedidos")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_pedido")
    private Long id;

    @Column(name = "cod_cliente",nullable = false)
    @NotNull(message = "Deve existir um cliente!")
    private Long cliente;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_pedido", referencedColumnName = "cod_pedido")
    private List<ItemPedido> itens = new ArrayList<>();

    @Column(name = "nro_valorTotal")
    private Double valorTotal = (double) 0;

    @Column(name = "cod_endereco",nullable = false)
    private Long endereco;

    @Column(name  = "ind_status")
    @Enumerated(EnumType.STRING)
    private StatusPedidoEnum statusPedido = StatusPedidoEnum.EM_ANDAMENTO;

}
