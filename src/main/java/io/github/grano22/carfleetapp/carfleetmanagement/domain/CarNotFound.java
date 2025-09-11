package io.github.grano22.carfleetapp.carfleetmanagement.domain;

import io.github.grano22.carfleetapp.shared.domain.InvalidDataGivenForOperation;

public class CarNotFound extends InvalidDataGivenForOperation {
    public CarNotFound(String id) {
        super("Car not found: " + id);
    }
}
