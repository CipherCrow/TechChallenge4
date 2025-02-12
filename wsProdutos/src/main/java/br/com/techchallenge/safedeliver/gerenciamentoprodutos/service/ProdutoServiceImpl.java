package br.com.techchallenge.safedeliver.gerenciamentoprodutos.service;

import br.com.techchallenge.safedeliver.gerenciamentoprodutos.domain.model.entities.Produto;
import br.com.techchallenge.safedeliver.gerenciamentoprodutos.exception.RegistroNotFoundException;
import br.com.techchallenge.safedeliver.gerenciamentoprodutos.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProdutoServiceImpl implements ProdutoService {

    ProdutoRepository produtoRepository;
    private static String idNotNull = "ID não pode ser nulo";

    @Override
    public Produto criar(Produto produto) {
        return produtoRepository.save(produto);
    }

    @Override
    public Produto atualizar(Produto produto,Long produtoID) {
        Objects.requireNonNull(produtoID, idNotNull);

        Produto produtoEncontrado = encontrarPeloId(produtoID);

        produtoEncontrado.setDescricao(produto.getDescricao());
        produtoEncontrado.setPreco(produto.getPreco());
        produtoEncontrado.setEstoque(produto.getEstoque());

        return produtoRepository.save(produtoEncontrado);
    }

    @Override
    public Produto atualizarQuantidade(Long produtoID, Integer quantidade) {
        Objects.requireNonNull(produtoID, idNotNull);

        Produto produtoEncontrado = encontrarPeloId(produtoID);

        produtoEncontrado.setEstoque(quantidade);

        return produtoRepository.save(produtoEncontrado);
    }

    @Override
    public Produto excluir(Long codigoProduto) {
        Objects.requireNonNull(codigoProduto, idNotNull);

        Produto produtoEncontrado = encontrarPeloId(codigoProduto);

        produtoEncontrado.setDeletado(true);
        return produtoRepository.save(produtoEncontrado);
    }

    @Override
    public List<Produto> findAll() {
        return produtoRepository.findAll();
    }

    @Override
    public List<Produto> findAllValidos() {
        return produtoRepository.findAllByDeletadoIsFalse();
    }

    @Override
    public Produto encontrarPeloId(Long produtoID) {
        Objects.requireNonNull(produtoID, idNotNull);

        return produtoRepository.findById(produtoID)
                .orElseThrow(() -> new RegistroNotFoundException("Produto"));
    }

    @Override
    public Produto validarEstoque(Long produtoID,
                                         Integer quantidade) {
        Produto produtoEncontrado = encontrarPeloId(produtoID);

        if(produtoEncontrado.getEstoque() < quantidade) {
            throw new IllegalArgumentException("Produto ["+produtoEncontrado.getDescricao()+"] sem estoque suficiente!");
        }

        return produtoEncontrado;
    }

    @Override
    public Produto validarReduzirEstoque(Long produtoID,
                                         Integer quantidade) {
        Produto produtoEncontrado = validarEstoque(produtoID, quantidade);
        produtoEncontrado.setEstoque(produtoEncontrado.getEstoque() - quantidade);

        return produtoRepository.save(produtoEncontrado);
    }
}
