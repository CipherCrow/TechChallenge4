package br.com.techchallenge.safedeliver.gerenciamentopedidos.service;

import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.ItemPedido;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.domain.model.entities.Produto;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.exception.RegistroNotFoundException;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.repository.ItemPedidoRepository;
import br.com.techchallenge.safedeliver.gerenciamentopedidos.repository.PedidoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@AllArgsConstructor
public class PedidoItemServiceImpl implements PedidoItemService {

    private ItemPedidoRepository itemPedidoRepository;
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    @Override
    public ItemPedido criar(Long codigoPedido, Long codigoItem, Integer quantidade) {
        Produto produto =  encontrarValidarProduto(codigoItem,quantidade);

        if(produto == null) {
            throw new RegistroNotFoundException("Produto ");
        }

        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setQuantidade(quantidade);
        itemPedido.setValorProduto(produto.getPreco());
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

        Produto produtoEncontrado = null;

        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:8080/produto/{id}"
                ,String.class
                ,codigoProduto
        );

        if(response.getStatusCode() == HttpStatus.NOT_FOUND){
            throw new RegistroNotFoundException("Produto");
        }else{
            try{
                JsonNode produtoJson = objectMapper.readTree((response.getBody()));
                produtoEncontrado = objectMapper.treeToValue(produtoJson, Produto.class);

                if(produtoEncontrado.getEstoque() < quantidade){
                    throw new IllegalArgumentException("Estoque não suficiente do produto selecionado");
                }
            }catch(Exception e){
                // tratar depois
            }
        }

        return produtoEncontrado;
    }
}
