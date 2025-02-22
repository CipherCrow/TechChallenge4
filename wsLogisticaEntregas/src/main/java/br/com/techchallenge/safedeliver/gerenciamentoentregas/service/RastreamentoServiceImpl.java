package br.com.techchallenge.safedeliver.gerenciamentoentregas.service;


import br.com.techchallenge.safedeliver.gerenciamentoentregas.client.EnderecoClient;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.client.PedidoClient;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Endereco;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Pedido;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Rastreamento;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.enums.StatusPedidoEnum;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.exception.ComunicacaoException;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.exception.RegistroNotFoundException;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.mapper.EnderecoMapper;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.mapper.PedidoMapper;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.repository.RastreamentoRepository;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@AllArgsConstructor
public class RastreamentoServiceImpl implements RastreamentoService {

    private RastreamentoRepository rastreamentoRepository;

    private EnderecoClient enderecoClient;

    private PedidoClient pedidoClient;

    private static final String IDNOTNULL = "ID não pode ser nulo";

    public Rastreamento criarRastreio(Long codPedido,
                                      Long codEndereco) {
        Pedido pedido = encontrarPedido(codPedido);
        if(!pedido.getStatusPedido().equals(StatusPedidoEnum.CONFIRMADO)) {
            throw new IllegalArgumentException("Só é possível criar rastreio para pedidos confirmados!");
        }

        Endereco endereco = encontrarEndereco(codEndereco);
        Rastreamento rastreamento = new Rastreamento();
        rastreamento.setPedido(pedido);
        rastreamento.setEndereco(endereco);
        return rastreamentoRepository.save(rastreamento);
    }

    @Override
    public Rastreamento encontrarPeloId(Long codRastreamento) {
        Objects.requireNonNull(codRastreamento, IDNOTNULL);

        return rastreamentoRepository.findById(codRastreamento)
                .orElseThrow(() -> new RegistroNotFoundException("Rastreamento "));
    }

    @Override
    public List<Rastreamento> encontrarAgruparPeloCep(String cep) {
        Objects.requireNonNull(cep, IDNOTNULL);

        return rastreamentoRepository.findRastreamentoByEndereco_CepLike(cep)
                .orElseThrow(() -> new RegistroNotFoundException("Rastreamento "));
    }

    @Override
    public Pedido encontrarPedido(Long codigoPedido) {
        try {
            return PedidoMapper.toEntity(
                    pedidoClient.pegarPedido(codigoPedido)
            );
        }
        catch (FeignException.NotFound e) {
            throw new RegistroNotFoundException("Pedido");
        } catch (FeignException e) {
            throw new ComunicacaoException("Pedido");
        }
    }

    @Override
    public Endereco encontrarEndereco(Long codigoEndereco) {
        try {
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
}