package br.com.techchallenge.safedeliver.gerenciamentoprodutos.repository;

import br.com.techchallenge.safedeliver.gerenciamentoprodutos.domain.model.entities.Produto;
import br.com.techchallenge.safedeliver.gerenciamentoprodutos.exception.RegistroNotFoundException;
import br.com.techchallenge.safedeliver.gerenciamentoprodutos.repository.ProdutoRepository;
import br.com.techchallenge.safedeliver.gerenciamentoprodutos.service.ProdutoServiceImpl;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ProdutoRepositoryTest {

    @Mock
    private ProdutoRepository produtoRepository;

    AutoCloseable openMocks;

    private List<Produto> produtosValidos = Arrays.asList(
            new Produto(2L, "Produto2", 5, 50.0, false),
            new Produto(2L, "Produto2", 5, 50.0, false)
    );

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception{
        openMocks.close();
    }

    @Nested
    @DisplayName("buscarTodosQueNãoEstãoDeletados")
    class CriarTests {
        @Test
        @DisplayName("Deve buscar uma lista com sucesso!")
        void findAllByDeletadoIsFalse() {
            when(produtoRepository.findAllByDeletadoIsFalse()).thenReturn(produtosValidos);

            List<Produto> result = produtoRepository.findAllByDeletadoIsFalse();

            assertThat(result).hasSize(2).containsAll(produtosValidos);
            verify(produtoRepository, times(1)).findAllByDeletadoIsFalse();
        }
    }
}