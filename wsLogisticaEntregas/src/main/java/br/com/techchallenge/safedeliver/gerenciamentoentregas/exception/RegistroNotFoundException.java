package br.com.techchallenge.safedeliver.gerenciamentoentregas.exception;

public class RegistroNotFoundException extends RuntimeException {
    public RegistroNotFoundException(String tabela) {
        super(tabela + " não encontrado com este ID!");
    }
}
