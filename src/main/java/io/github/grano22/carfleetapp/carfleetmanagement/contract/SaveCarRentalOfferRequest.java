package io.github.grano22.carfleetapp.carfleetmanagement.contract;

import io.github.grano22.carfleetapp.carrental.domain.CarRentalOfferStatus;
import io.github.grano22.carfleetapp.shared.validation.OnCreate;
import jakarta.validation.constraints.NotNull;

public record SaveCarRentalOfferRequest(
    @NotNull(groups = OnCreate.class) Double pricePerDay,
    Integer minRentalDays,
    Integer maxRentalDays,
    @NotNull(groups = OnCreate.class) CarRentalOfferStatus status,
    @NotNull(groups = OnCreate.class) String description
) {
}
