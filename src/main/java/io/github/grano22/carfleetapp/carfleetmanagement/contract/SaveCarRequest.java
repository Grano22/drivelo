package io.github.grano22.carfleetapp.carfleetmanagement.contract;

import io.github.grano22.carfleetapp.carfleetmanagement.domain.*;
import io.github.grano22.carfleetapp.shared.validation.OnCreate;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

import java.util.Set;

public record   SaveCarRequest(
    @NotNull(groups = OnCreate.class) String name,
    @NotNull(groups = OnCreate.class) String brand,
    String model,
    @Min(1900) @Max(9999) Integer year,
    @NotNull(groups = OnCreate.class) CarType type,
    @NotNull(groups = OnCreate.class) @Min(1) @Max(20) Integer maxCapacity,
    @NotNull(groups = OnCreate.class) CarStatus status,
    @NotNull(groups = OnCreate.class) GearboxType gearboxType,
    @NotNull(groups = OnCreate.class) EngineType engineType,
    @NotNull(groups = OnCreate.class) Set<CarAmenity> amenities,
    @URL String imageUrl,
    Integer mileage,
    Double fuelConsumption,
    @NotNull(groups = OnCreate.class) String description
) {
}
