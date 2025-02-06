package br.com.techchallenge.safedeliver.gerenciamentoclientes.domain.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "tb_cliente")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_cliente")
    private Long id;

    @Column(name = "des_nome",nullable = false)
    @NotNull(message = "Deve existir um nome!")
    private String nome;

    @Column(name = "nro_cpf",nullable = false)
    @NotNull(message = "Deve existir um cpf!")
    private String cpf;

    @Column(name = "des_email",nullable = false)
    @NotNull(message = "Deve existir um email!")
    private String email;

    @Column(name = "nro_telefone")
    private String telefone;

    @Column(name = "nro_idade")
    private int idade;

    @Column(name = "ind_deletado")
    private boolean deletado = false;

}
