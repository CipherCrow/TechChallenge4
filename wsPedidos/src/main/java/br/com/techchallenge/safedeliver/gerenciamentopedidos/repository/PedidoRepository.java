package br.com.techchallenge.safedeliver.gerenciamentopedidos.repository;

import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
