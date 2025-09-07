package io.github.grano22.carfleetapp.carfleetmanagement.domain;

public class CarNotFound extends RuntimeException {
    public CarNotFound(String id) {
        super("Car not found: " + id);
    }
}
