package io.github.grano22.carfleetapp.carfleetmanagement.assembler;

import io.github.grano22.carfleetapp.carfleetmanagement.Car;
import io.github.grano22.carfleetapp.carfleetmanagement.CarRentalOfferToViewMapper;
import io.github.grano22.carfleetapp.carfleetmanagement.contract.CarRentalOfferDetailsView;
import io.github.grano22.carfleetapp.carfleetmanagement.domain.CarOfferNotFound;
import io.github.grano22.carfleetapp.carfleetmanagement.infrastructure.persistance.CarRepository;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CarWithRentalDetailsAssembler {
    private final CarRepository carRepository;

    public CarWithRentalDetailsAssembler(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public CarRentalOfferDetailsView assemble(@NonNull UUID carId) {
        return carRepository
            .findById(carId)
            .map(Car::getRentalOffer)
            .map(CarRentalOfferToViewMapper::map)
            .orElseThrow(() -> new CarOfferNotFound(carId))
        ;
    }
}
