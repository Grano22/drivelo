package io.github.grano22.carfleetapp.carfleetmanagement.actions;

import io.github.grano22.carfleetapp.carfleetmanagement.Car;
import io.github.grano22.carfleetapp.carfleetmanagement.contract.SaveCarRequest;
import io.github.grano22.carfleetapp.carfleetmanagement.domain.CarNotFound;
import io.github.grano22.carfleetapp.carfleetmanagement.infrastructure.persistance.CarRepository;
import io.github.grano22.carfleetapp.shared.BuilderPatcher;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UpdateCarUseCase {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CarRepository carRepository;
    private final Clock clock;

    public UpdateCarUseCase(CarRepository carRepository, Clock clock) {
        this.carRepository = carRepository;
        this.clock = clock;
    }

    public Car execute(UUID carId, SaveCarRequest request) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFound(carId.toString()));
        Car.CarBuilder builder = car.toBuilder();

        List<String> changedFields = new ArrayList<>();
        builder = BuilderPatcher.patch(request, builder, Car.CarBuilder.class, changedFields::add);

        if (!changedFields.isEmpty()) {
            builder = builder.updatedAt(LocalDateTime.now(clock));
        }

        var updatedCar = builder.build();

        logger.info("Updating Car {} updated with fields: {}", carId, Strings.join(changedFields, ','));

        return carRepository.save(updatedCar);
    }
}
