package br.com.techchallenge.safedeliver.gerenciamentopedidos.exception;

public class ComunicacaoException extends RuntimeException {
    public ComunicacaoException(String tabela) {
        super("Erro ao comunicar com o serviço de " + tabela);
    }
}
