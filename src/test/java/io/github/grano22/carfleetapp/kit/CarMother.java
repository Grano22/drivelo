package io.github.grano22.carfleetapp.kit;

import io.github.grano22.carfleetapp.carfleetmanagement.Car;
import io.github.grano22.carfleetapp.carfleetmanagement.domain.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class CarMother {
    //public static String TOYOTA_SUPRA_MK5_UUID = "58387e55-268a-4170-9e2d-254de1ab87ab";

    public static Car bornToyotaSupraMk5() {
        return Car.builder()
            //.id(UUID.fromString(TOYOTA_SUPRA_MK5_UUID))
            .name("Toyota GR Supra (Automatic transmission variant)")
            .brand("Toyota")
            .model("GR Supra")
            .year(2025)
            .type(CarType.COUPE)
            .maxCapacity(2)
            .status(CarStatus.AVAILABLE)
            .gearboxType(GearboxType.AUTOMATIC)
            .engineType(EngineType.GASOLINE)
            .amenities(Set.of(CarAmenity.GPS))
            .description("")
            .imageUrl("")
            .fuelConsumption(21)
            .mileage(21)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build()
        ;
    }
}
