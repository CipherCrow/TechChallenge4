package br.com.techchallenge.safedeliver.gerenciamentoclientes.domain.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "tb_endereco")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Endereco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_endereco")
    private Long id;

    @Column(name = "nro_cep",nullable = false)
    @NotNull(message = "Deve existir um CEP!")
    private String cep;

    @Column(name = "des_cidade",nullable = false)
    @NotNull(message = "Deve existir uma cidade!")
    private String cidade;

    @Column(name = "des_descricao",nullable = false)
    @NotNull(message = "Deve existir a descricao do endereco!")
    private String descricao;

    @Column(name = "nro_numero")
    @NotNull(message = "Deve existir o numero da residencia!")
    private int numero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_cliente", referencedColumnName = "cod_cliente")
    private Cliente cliente;

    @Column(name = "ind_deletado")
    @Builder.Default
    private boolean deletado = false;

}
