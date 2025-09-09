package io.github.grano22.carfleetapp.shared.domain;

public class InvalidStateForOperation extends IllegalStateException {
    public InvalidStateForOperation(String message) {
        super(message);
    }
}
