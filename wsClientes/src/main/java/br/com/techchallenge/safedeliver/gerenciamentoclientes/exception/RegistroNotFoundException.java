package br.com.techchallenge.safedeliver.gerenciamentoclientes.exception;

public class RegistroNotFoundException extends RuntimeException {
    public RegistroNotFoundException(String tabela, Long id) {
        super(tabela + " não encontrado com este ID!");
    }
}
