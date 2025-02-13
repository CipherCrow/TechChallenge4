package br.com.techchallenge.safedeliver.gerenciamentoprodutos.domain.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "tb_produto")
@AllArgsConstructor
@NoArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_produto")
    private Long id;

    @Column(name = "des_descricao",nullable = false)
    @NotNull(message = "Deve existir uma descricao para o produto!")
    private String descricao;

    @Column(name = "qtd_estoque",nullable = false)
    @NotNull(message = "Deve existir uma quantidade em estoque!")
    private int estoque;

    @Column(name = "nro_preco",nullable = false)
    @NotNull(message = "Deve existir um preco!")
    private Double preco;

    @Column(name = "ind_deletado")
    private boolean deletado = false;

}
