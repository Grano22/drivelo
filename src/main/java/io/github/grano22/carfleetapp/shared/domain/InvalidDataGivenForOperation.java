package io.github.grano22.carfleetapp.shared.domain;

public class InvalidDataGivenForOperation extends IllegalArgumentException {
    public InvalidDataGivenForOperation(String message) {
        super(message);
    }

    public InvalidDataGivenForOperation(String message, Throwable cause) {
        super(message, cause);
    }
}
