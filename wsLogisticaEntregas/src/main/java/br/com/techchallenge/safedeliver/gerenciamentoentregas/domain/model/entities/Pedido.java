package br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities;

import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.enums.StatusPedidoEnum;
import jakarta.persistence.*;
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

    @JoinColumn(name = "cod_cliente",nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Cliente cliente;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_pedido", referencedColumnName = "cod_pedido")
    @Builder.Default
    private List<ItemPedido> itens = new ArrayList<>();

    @Column(name = "nro_valorTotal")
    @Builder.Default
    private Double valorTotal = (double) 0;

    @JoinColumn(name = "cod_endereco",nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Endereco endereco;

    @Column(name  = "ind_status")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatusPedidoEnum statusPedido = StatusPedidoEnum.EM_ANDAMENTO;

}
