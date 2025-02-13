package br.com.techchallenge.safedeliver.gerenciamentoentregas.repository;

import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Localizacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocalizacaoRepository extends JpaRepository<Localizacao, Long> {
    Optional<List<Localizacao>> findLocalizacaosByRastreamento_Id(Long idRastreamento);
}
