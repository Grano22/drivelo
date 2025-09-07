package io.github.grano22.carfleetapp.carrental.contract;

import java.util.List;

public record UserRentedCarView(
    String id,
    String brand,
    String model,
    int year,
    String vehicleType,
    int maxCapacity,
    String carStatus,
    String gearboxType,
    String engineType,
    List<String> amenities,
    String carDescription,
    String imageUrl,
    int mileage,
    Double fuelConsumption,
    String rentedFrom,
    String rentedUntil,
    double dailyFee,
    String createdAt,
    String updatedAt
) {
}
