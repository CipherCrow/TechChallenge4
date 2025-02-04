package br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

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
    private int idCliente;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_pedido", referencedColumnName = "cod_pedido")
    private ArrayList<ItemPedido> pedidos;

    @Column(name = "nro_valorTotal")
    private Double valorTotal;

    @Column(name = "cod_endereco",nullable = false)
    private int idEndereco;

}
