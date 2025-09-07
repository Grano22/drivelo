package io.github.grano22.carfleetapp.carfleetmanagement.actions;

import io.github.grano22.carfleetapp.carfleetmanagement.Car;
import io.github.grano22.carfleetapp.carfleetmanagement.contract.SaveCarRentalOfferRequest;
import io.github.grano22.carfleetapp.carfleetmanagement.domain.CarNotFound;
import io.github.grano22.carfleetapp.carfleetmanagement.infrastructure.persistance.CarRepository;
import io.github.grano22.carfleetapp.carrental.CarRentalOffer;
import io.github.grano22.carfleetapp.carrental.infrastructure.persistance.CarRentalOfferRepository;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CreateCarRentalOfferUseCase {
    private final CarRepository carRepository;
    private final CarRentalOfferRepository carRentalOfferRepository;
    private final Clock clock;

    public CreateCarRentalOfferUseCase(CarRepository carRepository, CarRentalOfferRepository carRentalOfferRepository, Clock clock) {
        this.carRepository = carRepository;
        this.carRentalOfferRepository = carRentalOfferRepository;
        this.clock = clock;
    }

    public void execute(@NonNull UUID carId, @NonNull SaveCarRentalOfferRequest request) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFound(carId.toString()));

        if (car.getRentalOffer() != null) {
            throw new IllegalStateException("Car " + carId + " already has a rental offer");
        }

        LocalDateTime now = LocalDateTime.now(clock);
        CarRentalOffer offer = CarRentalOffer.builder()
            .car(car)
            .pricePerDay(request.pricePerDay())
            .minRentalDays(request.minRentalDays())
            .maxRentalDays(request.maxRentalDays())
            .status(request.status())
            .description(request.description())
            .createdAt(now)
            .updatedAt(now)
            .build()
        ;

        carRentalOfferRepository.save(offer);
    }
}
