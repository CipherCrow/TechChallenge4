package br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "tb_entregas")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Entrega {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_entrega")
    private Long id;

    @Column(name = "cod_pedido",nullable = false)
    private int codigoPedido;

    @Column(name = "cod_endereco")
    private int codigoEnderecoEntrega;

    @ManyToOne(fetch = FetchType.LAZY)
    private Entregador entregador;

    @Column(name = "nro_PrazoDias")
    private int prazoEntrega;

    @Column(name = "nro_vlrTotalItem")
    private Double valorTotalItem;
}
