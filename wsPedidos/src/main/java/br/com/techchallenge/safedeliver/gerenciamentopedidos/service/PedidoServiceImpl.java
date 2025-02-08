package br.com.techchallenge.safedeliver.gerenciamentopedidos.service;

import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.Pedido;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoServiceImpl implements PedidoService {

    @Override
    public Pedido criar(Long codigoCliente) {
        return null;
    }

    @Override
    public Pedido inserirItem(Long codigoPedido, Long codigoItem, int quantidade) {
        return null;
    }

    @Override
    public Pedido removerItem(Long codigoPedido, Long codigoItem) {
        return null;
    }

    @Override
    public Pedido findById(Long codigoPedido) {
        return null;
    }

    @Override
    public List<Pedido> listarTodos() {
        return List.of();
    }

    @Override
    public Pedido confirmarPedido(Long codigoPedido, Long codigoEnderecoEntrega) {
        return null;
    }

    @Override
    public boolean cancelarPedido(Long codigoPedido) {
        return false;
    }

    @Override
    public Double obterValorTotalPedido(Long codigoPedido) {
        return 0.0;
    }
}
