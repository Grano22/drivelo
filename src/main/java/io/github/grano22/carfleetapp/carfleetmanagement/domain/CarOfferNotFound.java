package io.github.grano22.carfleetapp.carfleetmanagement.domain;

import io.github.grano22.carfleetapp.shared.domain.InvalidDataGivenForOperation;

import java.util.UUID;

public class CarOfferNotFound extends InvalidDataGivenForOperation {
    public CarOfferNotFound(UUID id) {
        super("Car offer not found: " + id);
    }
}
