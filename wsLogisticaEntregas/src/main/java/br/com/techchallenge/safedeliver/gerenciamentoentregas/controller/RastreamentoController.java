package br.com.techchallenge.safedeliver.gerenciamentoentregas.controller;

import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Localizacao;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.domain.model.entities.Rastreamento;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.dto.LocalizacaoDTO;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.dto.RastreamentoDTO;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.mapper.LocalizacaoMapper;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.mapper.RastreamentoMapper;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.service.LocalizacaoService;
import br.com.techchallenge.safedeliver.gerenciamentoentregas.service.RastreamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/rastreamento")
@RequiredArgsConstructor
public class RastreamentoController {

    @Autowired
    private final RastreamentoService rastreamentoService;

    @Autowired
    private final LocalizacaoService localizacaoService;

    @PostMapping("/criarRastreamento")
    public ResponseEntity<RastreamentoDTO> criar(@Valid @RequestParam Long codPedido,
                                                 @Valid @RequestParam Long codEndereco){
        return ResponseEntity.status(HttpStatus.CREATED).body(
            RastreamentoMapper.toDTO(
                    rastreamentoService.criarRastreio(codPedido,codEndereco)
            )
        );
    }

    @GetMapping("/buscarRastreamento/{id}")
    public ResponseEntity<RastreamentoDTO> encontrar(@Valid @PathVariable Long id){
        return ResponseEntity.status(HttpStatus.FOUND).body(
                RastreamentoMapper.toDTO(
                    rastreamentoService.encontrarPeloId(id)
            )
        );
    }

    @GetMapping("/buscarRastreamentoCep/{cep}")
    public ResponseEntity<List<Rastreamento>> encontrarPeloCep(@Valid @PathVariable String cep){
        return ResponseEntity.status(HttpStatus.FOUND).body(
            rastreamentoService.encontrarAgruparPeloCep(cep)
        );
    }

    @PostMapping("/adicionarLocalizacao/{id}")
    public ResponseEntity<LocalizacaoDTO> adicionar(@RequestBody LocalizacaoDTO localizacao,
                                                    @Valid @PathVariable Long id){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                LocalizacaoMapper.toDTO(
                        localizacaoService.adicionar(LocalizacaoMapper.toEntity(localizacao), id)
                )
        );
    }

    @GetMapping("/visualizarHistorico/{id}")
    public ResponseEntity<List<Localizacao>> encontrarTodos(@Valid @PathVariable Long id){
        return ResponseEntity.status(HttpStatus.FOUND).body(
                localizacaoService.buscarPeloRastreamento(id)
        );
    }
}
