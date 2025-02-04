package br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@Entity(name = "tb_entregador")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Entregador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_entregador")
    private Long id;

    @Column(name = "des_nome",nullable = false)
    @NotNull(message = "Deve existir um nome para o entregador!")
    private String nome;

    @Column(name = "des_cpf",nullable = false)
    @NotNull(message = "Deve existir um cpf para o entregador!")
    private String cpf;

    @Column(name = "des_email")
    private String email;

}
