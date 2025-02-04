package br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "tb_itemPedidos")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_itemPedido")
    private Long id;

    @Column(name = "nro_qtd",nullable = false)
    private int quantidade;

    @Column(name = "nro_numero")
    private Double valorProduto;

    @Column(name = "nro_vlrTotalItem")
    private Double valorTotalItem;
}
