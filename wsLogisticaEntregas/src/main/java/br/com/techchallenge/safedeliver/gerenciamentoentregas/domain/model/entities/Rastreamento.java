package br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "tb_rastreamento")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Rastreamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_entrega")
    private Long id;

    @Column(name = "cod_pedido",nullable = false)
    @OneToOne(cascade = CascadeType.ALL)
    private Pedido pedido;

    @Column(name = "cod_endereco")
    @OneToOne(cascade = CascadeType.ALL)
    private Endereco endereco;
    
}
