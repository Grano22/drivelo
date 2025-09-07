package io.github.grano22.carfleetapp.carfleetmanagement.contract;

import java.util.List;

public record CarRentalOfferDetailsView(
    String id,
    String brand,
    String model,
    int year,
    String vehicleType,
    int maxCapacity,
    double pricePerDay,
    String status,
    String carStatus,
    String gearboxType,
    String engineType,
    List<String> amenities,
    String description,
    String carDescription,
    String imageUrl,
    int mileage,
    Double fuelConsumption,
    Integer minRentalDays,
    Integer maxRentalDays,
    String createdAt,
    String updatedAt
) {
}
