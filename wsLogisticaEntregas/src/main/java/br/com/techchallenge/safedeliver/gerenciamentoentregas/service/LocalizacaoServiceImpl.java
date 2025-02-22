package br.com.techchallenge.safedeliver.gerenciamentoentregas.service;


import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Localizacao;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Rastreamento;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.exception.RegistroNotFoundException;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.repository.LocalizacaoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class LocalizacaoServiceImpl implements LocalizacaoService {

    private LocalizacaoRepository localizacaoRepository;

    private RastreamentoService rastreamentoService;

    private static final String IDNOTNULL = "ID não pode ser nulo";

    @Override
    public Localizacao adicionar(Localizacao localizacao,Long idRastreamento) {
        Rastreamento rastreamentoEncontrado = rastreamentoService.encontrarPeloId(idRastreamento);
        localizacao.setRastreamento(rastreamentoEncontrado);

        return localizacaoRepository.save(localizacao);
    }

    @Override
    public List<Localizacao> buscarPeloRastreamento(Long codRastreamento) {
        Objects.requireNonNull(codRastreamento, IDNOTNULL);

        return localizacaoRepository.findLocalizacaosByRastreamento_Id(codRastreamento)
                .orElseThrow(() -> new RegistroNotFoundException("Localizacao "));
    }
}