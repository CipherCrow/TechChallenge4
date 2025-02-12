package br.com.techchallenge.safedeliver.gerenciamentoentregas.exception;

public class ComunicacaoException extends RuntimeException {
    public ComunicacaoException(String tabela) {
        super("Erro ao comunicar com o serviço de " + tabela);
    }
}
