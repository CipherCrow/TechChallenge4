package br.com.techchallenge.safedeliver.gerenciamentopedidos.service;

import br.com.techchallenge.safedeliver.gerenciamentopedidos.client.ClienteClient;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.client.EnderecoClient;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.client.ProdutoClient;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.Cliente;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.Endereco;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.ItemPedido;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.Pedido;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.enums.StatusPedidoEnum;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.exception.ComunicacaoException;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.exception.RegistroNotFoundException;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.mapper.ClienteMapper;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.mapper.EnderecoMapper;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.repository.PedidoRepository;
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final ProdutoClient produtoClient;
    private PedidoRepository pedidoRepository;
    private PedidoItemService pedidoItemService;
    private ClienteClient clienteClient;
    private EnderecoClient enderecoClient;

    private static final String IDNOTNULL = "ID não pode ser nulo";

    @Override
    public Pedido criar(Long clienteInformado) {
        Objects.requireNonNull(clienteInformado, IDNOTNULL);
        Cliente cliente = encontrarCliente(clienteInformado);

        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .statusPedido(StatusPedidoEnum.EM_ANDAMENTO)
                .build();

        return pedidoRepository.save(pedido);
    }

    @Override
    public Pedido inserirItem(Long codigoPedido, Long codigoItem, int quantidade) {
        Objects.requireNonNull(codigoItem, IDNOTNULL);
        Objects.requireNonNull(codigoPedido, IDNOTNULL);

        Pedido pedidoEncontrado = encontrarPeloId(codigoPedido);
        ItemPedido itemPedido =  pedidoItemService.criar(codigoPedido, codigoItem, quantidade);
        pedidoEncontrado.getItens().add(itemPedido);

        return pedidoRepository.save(pedidoEncontrado);
    }

    @Override
    public Pedido removerItem(Long codigoPedido, Long codigoItemPedido) {
        Objects.requireNonNull(codigoItemPedido, IDNOTNULL);
        Objects.requireNonNull(codigoPedido, IDNOTNULL);

        Pedido pedidoEncontrado = encontrarPeloId(codigoPedido);
        ItemPedido itemPedido =
            pedidoEncontrado.getItens().stream().filter(
                item -> item.getId().equals(codigoItemPedido)
            ).findFirst().orElse(null);

        if(itemPedido == null){
            throw new RegistroNotFoundException("Item");
        }

        pedidoEncontrado.getItens().remove(itemPedido);

        return pedidoRepository.save(pedidoEncontrado);
    }

    @Override
    public Pedido encontrarPeloId(Long codigoPedido) {
        Objects.requireNonNull(codigoPedido, IDNOTNULL);

        return pedidoRepository.findById(codigoPedido).orElseThrow(
                () -> new RegistroNotFoundException("Pedido"));
    }

    @Override
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    @Override
    public Pedido confirmarPedido(Long codigoPedido, Long codigoEnderecoEntrega) {
        Objects.requireNonNull(codigoEnderecoEntrega, IDNOTNULL);

        Pedido pedido = encontrarPeloId(codigoPedido);
        if(pedido.getStatusPedido() != StatusPedidoEnum.EM_ANDAMENTO){
            throw new IllegalArgumentException("Apenas pedidos em andamento podem ser confirmados!");
        }
        pedido.setStatusPedido(StatusPedidoEnum.CONFIRMADO);

        validarProdutosParaFechamento(pedido.getItens());

        Endereco endereco = encontrarEndereco(codigoEnderecoEntrega);
        pedido.setEndereco(endereco);
        return pedidoRepository.save(pedido);
    }

    @Override
    public Pedido cancelarPedido(Long codigoPedido) {
        Pedido pedido = encontrarPeloId(codigoPedido);
        if(pedido.getStatusPedido() != StatusPedidoEnum.EM_ANDAMENTO){
            throw new IllegalArgumentException("Apenas pedidos em andamento podem ser cancelados!");
        }
        pedido.setStatusPedido(StatusPedidoEnum.CANCELADO);
        return pedidoRepository.save(pedido);
    }

    @Override
    public Pedido atualizarValorTotal(Long codigoPedido) {
        Objects.requireNonNull(codigoPedido, IDNOTNULL);
        Pedido pedido = encontrarPeloId(codigoPedido);

        Double valorTotal =
                pedido.getItens().stream()
                        .map(ItemPedido::getValorVendidoUnitario)
                        .reduce(0.0,(Double vlrTotal, Double vlrAtual ) ->
                                vlrTotal + vlrAtual
                        );

        pedido.setValorTotal(valorTotal);
        return pedidoRepository.save(pedido);
    }

    @Override
    public Cliente encontrarCliente(Long codigoCliente) {
        try {
            return ClienteMapper.toEntity(
                    clienteClient.encontrarCliente(codigoCliente)
            );
        }
        catch (FeignException.NotFound e) {
            throw new RegistroNotFoundException("Cliente");
        } catch (FeignException e) {
            throw new ComunicacaoException("Cliente");
        }
    }

    @Override
    public Endereco encontrarEndereco(Long codigoEndereco) {
        try{
            return EnderecoMapper.toEntity(
                    enderecoClient.encontrarEndereco(codigoEndereco)
            );
        }
            catch (FeignException.NotFound e) {
            throw new RegistroNotFoundException("Endereco");
        } catch (FeignException e) {
            throw new ComunicacaoException("Endereco");
        }
    }

    @Override
    public boolean validarProdutosParaFechamento(List<ItemPedido> itens) {
        try{
            for(ItemPedido itemPedido : itens ){
                Long codProduto = itemPedido.getProduto().getId();
                Integer quantidade = itemPedido.getQuantidade();
                produtoClient.validaReduzirEstoque(codProduto, quantidade);
            }
            return true;
        }
        catch (FeignException.NotFound e) {
            throw new RegistroNotFoundException("Produto");
        }catch (FeignException.BadRequest e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (FeignException e) {
            throw new ComunicacaoException("Produto");
        }
    }
}
