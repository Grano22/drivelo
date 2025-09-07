package io.github.grano22.carfleetapp.carfleetmanagement.contract;

import io.github.grano22.carfleetapp.shared.validation.OnCreate;
import jakarta.validation.constraints.NotNull;

public record SaveCarWithRentalOfferRequest(
    @NotNull(groups = OnCreate.class) SaveCarRequest car,
    @NotNull(groups = OnCreate.class) SaveCarRentalOfferRequest rentalOfferDetails
) {
}
