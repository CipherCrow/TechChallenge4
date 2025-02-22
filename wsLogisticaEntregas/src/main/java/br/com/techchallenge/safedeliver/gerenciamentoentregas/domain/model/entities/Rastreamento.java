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

    @PrimaryKeyJoinColumn(name = "cod_pedido")
    @OneToOne(cascade = CascadeType.ALL)
    private Pedido pedido;

    @PrimaryKeyJoinColumn(name = "cod_endereco")
    @OneToOne(cascade = CascadeType.ALL)
    private Endereco endereco;
    
}
