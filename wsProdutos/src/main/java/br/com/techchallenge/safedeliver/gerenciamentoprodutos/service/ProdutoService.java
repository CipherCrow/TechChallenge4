package br.com.techchallenge.safedeliver.gerenciamentoprodutos.service;

import br.com.techchallenge.safedeliver.gerenciamentoprodutos.domain.model.entities.Produto;

import java.util.List;

public interface ProdutoService {
    Produto criar(Produto produto);
    Produto atualizar(Produto produto,Long produtoID);
    Produto atualizarQuantidade(Long produtoID,Integer quantidade);
    Produto excluir(Long produtoID);
    List<Produto> findAll();
    List<Produto> findAllValidos();
    Produto encontrarPeloId(Long produtoID);
    Produto validarReduzir(Long produtoID,Integer quantidade);
}
