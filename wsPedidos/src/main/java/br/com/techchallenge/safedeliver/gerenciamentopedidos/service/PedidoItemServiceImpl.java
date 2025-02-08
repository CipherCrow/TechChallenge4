package br.com.techchallenge.safedeliver.gerenciamentopedidos.service;

import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.ItemPedido;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoItemServiceImpl implements PedidoItemService {

    @Override
    public ItemPedido criar(Long codigoCliente) {
        return null;
    }

    @Override
    public boolean removerItem(Long codigoPedido, Long codigoItem) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ItemPedido findById(Long codigoPedido) {
        return null;
    }

    @Override
    public List<ItemPedido> listarTodosDoPedido(Long codigoPedido) {
        return List.of();
    }
}
