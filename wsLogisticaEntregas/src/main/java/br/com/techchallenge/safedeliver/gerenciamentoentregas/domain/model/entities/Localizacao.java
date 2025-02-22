package br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity(name = "tb_localizacao")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Localizacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_localizacao")
    private Long id;

    @Column(name = "des_longitude")
    private String longitude;

    @Column(name = "des_latitude")
    private String latitude;

    @Column(name = "dat_registro")
    private LocalDateTime horaRegistro;

    @JoinColumn(name = "cod_rastreamento")
    @ManyToOne(fetch = FetchType.LAZY)
    private Rastreamento rastreamento;
}
