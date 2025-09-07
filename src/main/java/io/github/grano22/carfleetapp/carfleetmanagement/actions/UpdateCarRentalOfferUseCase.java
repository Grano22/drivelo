package io.github.grano22.carfleetapp.carfleetmanagement.actions;

import io.github.grano22.carfleetapp.carfleetmanagement.Car;
import io.github.grano22.carfleetapp.carfleetmanagement.contract.SaveCarRentalOfferRequest;
import io.github.grano22.carfleetapp.carfleetmanagement.contract.SaveCarWithRentalOfferRequest;
import io.github.grano22.carfleetapp.carfleetmanagement.domain.CarNotFound;
import io.github.grano22.carfleetapp.carfleetmanagement.infrastructure.persistance.CarRepository;
import io.github.grano22.carfleetapp.carrental.CarRentalOffer;
import io.github.grano22.carfleetapp.carrental.infrastructure.persistance.CarRentalOfferRepository;
import io.github.grano22.carfleetapp.shared.BuilderPatcher;
import lombok.NonNull;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UpdateCarRentalOfferUseCase {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CarRepository carRepository;
    private final CarRentalOfferRepository carRentalOfferRepository;
    private final Clock clock;

    public UpdateCarRentalOfferUseCase(CarRepository carRepository, CarRentalOfferRepository carRentalOfferRepository, Clock clock) {
        this.carRepository = carRepository;
        this.carRentalOfferRepository = carRentalOfferRepository;
        this.clock = clock;
    }

    @Transactional
    public void execute(@NonNull UUID carId, @NonNull SaveCarRentalOfferRequest request) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFound(carId.toString()));

        if (car.getRentalOffer() == null) {
            throw new IllegalStateException("Car " + carId + " has no rental offer");
        }

        CarRentalOffer.CarRentalOfferBuilder builder = car.getRentalOffer().toBuilder();

        List<String> changedFields = new ArrayList<>();
        builder = BuilderPatcher.patch(request, builder, CarRentalOffer.CarRentalOfferBuilder.class, changedFields::add);

        if (!changedFields.isEmpty()) {
            builder = builder.updatedAt(LocalDateTime.now(clock));
        }

        var updatedCarRentalOffer = builder.build();

        logger.info("Updating Car {} with rental details {} updated with fields: {}", carId, updatedCarRentalOffer.getId(), Strings.join(changedFields, ','));
        carRentalOfferRepository.save(updatedCarRentalOffer);
    }
}
