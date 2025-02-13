package br.com.techchallenge.safedeliver.gerenciamentoentregas.repository;

import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Rastreamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RastreamentoRepository extends JpaRepository<Rastreamento, Long> {
    Optional<List<Rastreamento>> findRastreamentoByEndereco_CepLike(String cep);
}
