package io.github.grano22.carfleetapp.carrental.contract;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CarRentalRequest(
    @NotNull UUID offerId,
    @NotNull OffsetDateTime from,
    @NotNull @Future OffsetDateTime to
) {
}
