package br.com.techchallenge.safedeliver.gerenciamentoprodutos.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import br.com.techchallenge.safedeliver.gerenciamentoprodutos.domain.model.entities.Produto;
import br.com.techchallenge.safedeliver.gerenciamentoprodutos.exception.RegistroNotFoundException;
import br.com.techchallenge.safedeliver.gerenciamentoprodutos.repository.ProdutoRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ProdutoServiceTest {

    private ProdutoServiceImpl produtoService;

    @Mock
    private ProdutoRepository produtoRepository;

    AutoCloseable openMocks;

    private Produto produto;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        this.produtoService = new ProdutoServiceImpl(produtoRepository);
        // Exemplo de produto para os testes
        produto = new Produto(1L, "Descricao", 10, 100.0, false);
    }

    @AfterEach
    void tearDown() throws Exception{
        openMocks.close();
    }

    @Nested
    @DisplayName("criarProduto")
    class CriarTests {
        @Test
        @DisplayName("Deve criar um produto com sucesso")
        void criarProdutoComSucesso() {
            when(produtoRepository.save(produto)).thenReturn(produto);

            Produto result = produtoService.criar(produto);

            verify(produtoRepository, times(1)).save(produto);
            assertThat(result).isEqualTo(produto);
        }
    }

    @Nested
    @DisplayName("Atualizar")
    class AtualizarTests {
        @Test
        @DisplayName("Deve atualizar um produto com sucesso")
        void atualizarProdutoComSucesso() {
            Produto produtoAtualizado = new Produto(null, "Nova Descricao", 20, 200.0, false);

            when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));
            when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Produto result = produtoService.atualizar(produtoAtualizado, produto.getId());

            assertThat(result.getDescricao()).isEqualTo("Nova Descricao");
            assertThat(result.getEstoque()).isEqualTo(20);
            assertThat(result.getPreco()).isEqualTo(200.0);
            verify(produtoRepository, times(1)).findById(produto.getId());
            verify(produtoRepository, times(1)).save(any(Produto.class));
        }

        @Test
        @DisplayName("Deve lançar NullPointerException se produtoID for nulo")
        void atualizarProdutoComIdNulo() {
            Produto produtoAtualizado = new Produto(null, "Nova Descricao", 20, 200.0, false);

            assertThatThrownBy(() -> produtoService.atualizar(produtoAtualizado, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("ID não pode ser nulo");
        }

        @Test
        @DisplayName("Deve lançar RegistroNotFoundException se produto não existir")
        void atualizarProdutoNaoExistente() {
            Produto produtoAtualizado = new Produto(null, "Nova Descricao", 20, 200.0, false);

            when(produtoRepository.findById(produto.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> produtoService.atualizar(produtoAtualizado, produto.getId()))
                    .isInstanceOf(RegistroNotFoundException.class)
                    .hasMessageContaining("Produto");
        }
    }

    @Nested
    @DisplayName("Atualizar Quantidade")
    class AtualizarQuantidadeTests {
        @Test
        @DisplayName("Deve atualizar a quantidade do produto com sucesso")
        void atualizarQuantidadeComSucesso() {
            when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));
            when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Integer novaQuantidade = 50;
            Produto result = produtoService.atualizarQuantidade(produto.getId(), novaQuantidade);

            assertThat(result.getEstoque()).isEqualTo(novaQuantidade);
            verify(produtoRepository, times(1)).findById(produto.getId());
            verify(produtoRepository, times(1)).save(any(Produto.class));
        }

        @Test
        @DisplayName("Deve lançar NullPointerException se produtoID for nulo")
        void atualizarQuantidadeComIdNulo() {
            assertThatThrownBy(() -> produtoService.atualizarQuantidade(null, 50))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("ID não pode ser nulo");
        }

        @Test
        @DisplayName("Deve lançar RegistroNotFoundException se produto não existir")
        void atualizarQuantidadeProdutoNaoExistente() {
            when(produtoRepository.findById(produto.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> produtoService.atualizarQuantidade(produto.getId(), 50))
                    .isInstanceOf(RegistroNotFoundException.class)
                    .hasMessageContaining("Produto");
        }
    }

    @Nested
    @DisplayName("excluir")
    class ExcluirTests {
        @Test
        @DisplayName("Deve marcar produto como deletado com sucesso")
        void excluirProdutoComSucesso() {
            when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));
            when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Produto result = produtoService.excluir(produto.getId());

            assertThat(result.isDeletado()).isTrue();
            verify(produtoRepository, times(1)).findById(produto.getId());
            verify(produtoRepository, times(1)).save(any(Produto.class));
        }

        @Test
        @DisplayName("Deve lançar NullPointerException se produtoID for nulo")
        void excluirProdutoComIdNulo() {
            assertThatThrownBy(() -> produtoService.excluir(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("ID não pode ser nulo");
        }

        @Test
        @DisplayName("Deve lançar RegistroNotFoundException se produto não existir")
        void excluirProdutoNaoExistente() {
            when(produtoRepository.findById(produto.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> produtoService.excluir(produto.getId()))
                    .isInstanceOf(RegistroNotFoundException.class)
                    .hasMessageContaining("Produto");
        }
    }

    @Nested
    @DisplayName("findAll e findAllValidos")
    class FindTests {
        @Test
        @DisplayName("Deve retornar todos os produtos")
        void findAll() {
            List<Produto> produtos = Arrays.asList(
                    produto,
                    new Produto(2L, "Produto2", 5, 50.0, false)
            );
            when(produtoRepository.findAll()).thenReturn(produtos);

            List<Produto> result = produtoService.findAll();

            assertThat(result).hasSize(2).containsAll(produtos);
            verify(produtoRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Deve retornar todos os produtos válidos")
        void findAllValidos() {
            List<Produto> produtosValidos = Arrays.asList(
                    produto,
                    new Produto(2L, "Produto2", 5, 50.0, false)
            );
            when(produtoRepository.findAllByDeletadoIsFalse()).thenReturn(produtosValidos);

            List<Produto> result = produtoService.findAllValidos();

            assertThat(result).hasSize(2).containsAll(produtosValidos);
            verify(produtoRepository, times(1)).findAllByDeletadoIsFalse();
        }
    }

    @Nested
    @DisplayName("encontrarPeloId")
    class EncontrarPeloIdTests {
        @Test
        @DisplayName("Deve retornar o produto se encontrado")
        void encontrarProdutoComSucesso() {
            when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));

            Produto result = produtoService.encontrarPeloId(produto.getId());

            assertThat(result).isEqualTo(produto);
            verify(produtoRepository, times(1)).findById(produto.getId());
        }

        @Test
        @DisplayName("Deve lançar NullPointerException se produtoID for nulo")
        void encontrarProdutoComIdNulo() {
            assertThatThrownBy(() -> produtoService.encontrarPeloId(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("ID não pode ser nulo");
        }

        @Test
        @DisplayName("Deve lançar RegistroNotFoundException se produto não for encontrado")
        void encontrarProdutoNaoEncontrado() {
            when(produtoRepository.findById(produto.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> produtoService.encontrarPeloId(produto.getId()))
                    .isInstanceOf(RegistroNotFoundException.class)
                    .hasMessageContaining("Produto");
        }
    }

    @Nested
    @DisplayName("validarEstoque")
    class ValidarEstoqueTests {
        @Test
        @DisplayName("Deve validar o estoque com sucesso")
        void validarEstoqueComSucesso() {
            when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));

            // Estoque suficiente
            Produto result = produtoService.validarEstoque(produto.getId(), 5);

            assertThat(result).isEqualTo(produto);
            verify(produtoRepository, times(1)).findById(produto.getId());
        }

        @Test
        @DisplayName("Deve lançar exceção se o estoque for insuficiente")
        void validarEstoqueInsuficiente() {
            produto.setEstoque(3);
            when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));

            assertThatThrownBy(() -> produtoService.validarEstoque(produto.getId(), 5))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Produto [Descricao] sem estoque suficiente!");
        }

        @Test
        @DisplayName("Deve lançar NullPointerException se produtoID for nulo")
        void validarEstoqueComIdNulo() {
            assertThatThrownBy(() -> produtoService.validarEstoque(null, 5))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("ID não pode ser nulo");
        }
    }

    @Nested
    @DisplayName("validarReduzirEstoque")
    class ValidarReduzirEstoqueTests {
        @Test
        @DisplayName("Deve reduzir o estoque com sucesso")
        void reduzirEstoqueComSucesso() {
            when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));
            when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> invocation.getArgument(0));

            int quantidade = 5;
            int estoqueInicial = produto.getEstoque();
            Produto result = produtoService.validarReduzirEstoque(produto.getId(), quantidade);

            assertThat(result.getEstoque()).isEqualTo(estoqueInicial - quantidade);
            verify(produtoRepository, times(1)).findById(produto.getId());
            verify(produtoRepository, times(1)).save(any(Produto.class));
        }

        @Test
        @DisplayName("Deve lançar exceção se o estoque for insuficiente para reduzir")
        void reduzirEstoqueInsuficiente() {
            produto.setEstoque(3);
            when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));

            assertThatThrownBy(() -> produtoService.validarReduzirEstoque(produto.getId(), 5))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Produto [Descricao] sem estoque suficiente!");
        }
    }
}