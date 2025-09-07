package io.github.grano22.carfleetapp.carrental.contract;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CarReturnRequest(@NotNull UUID rentalId) {
}
