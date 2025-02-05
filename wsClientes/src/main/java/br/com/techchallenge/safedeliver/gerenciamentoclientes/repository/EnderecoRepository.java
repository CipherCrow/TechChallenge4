package br.com.techchallenge.safedeliver.gerenciamentoclientes.repository;

import br.com.techchallenge.safedeliver.gerenciamentoclientes.domain.model.entities.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
}
