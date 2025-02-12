package br.com.techchallenge.safedeliver.gerenciamentopedidos.service;

import br.com.techchallenge.safedeliver.gerenciamentopedidos.client.ProdutoClient;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.ItemPedido;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.Produto;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.exception.ComunicacaoException;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.exception.RegistroNotFoundException;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.mapper.ProdutoMapper;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.repository.ItemPedidoRepository;
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PedidoItemServiceImpl implements PedidoItemService {

    private ItemPedidoRepository itemPedidoRepository;
    private ProdutoClient produtoClient;

    @Override
    public ItemPedido criar(Long codigoPedido, Long codigoItem, Integer quantidade) {
        Produto produto =  encontrarValidarProduto(codigoItem,quantidade);

        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setProduto(produto);
        itemPedido.setQuantidade(quantidade);
        itemPedido.setValorVendidoUnitario(produto.getPreco());
        itemPedido.setValorTotalItem(quantidade * produto.getPreco());

        return itemPedido;
    }
/*
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
*/
    @Override
    public Produto encontrarValidarProduto(Long codigoProduto, Integer quantidade) {
        try{
            return ProdutoMapper.toEntity(
                produtoClient.validaEstoque(codigoProduto,quantidade)
            );
        }
        catch (FeignException.NotFound e) {
            throw new RegistroNotFoundException("Produto");
        } catch (FeignException e) {
            throw new ComunicacaoException("Produto");
        }

    }
}
