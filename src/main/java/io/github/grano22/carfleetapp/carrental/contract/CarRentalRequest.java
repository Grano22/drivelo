package io.github.grano22.carfleetapp.carrental.contract;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record CarRentalRequest(
    @NotNull UUID offerId,
    @NotNull LocalDateTime from,
    @NotNull @Future LocalDateTime to
) {
}
