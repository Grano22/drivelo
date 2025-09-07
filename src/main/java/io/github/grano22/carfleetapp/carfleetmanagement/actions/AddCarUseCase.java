package io.github.grano22.carfleetapp.carfleetmanagement.actions;

import io.github.grano22.carfleetapp.carfleetmanagement.Car;
import io.github.grano22.carfleetapp.carfleetmanagement.contract.SaveCarRequest;
import io.github.grano22.carfleetapp.carfleetmanagement.infrastructure.persistance.CarRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
public class AddCarUseCase {
    private final CarRepository carRepository;
    private final Clock clock;

    public AddCarUseCase(CarRepository carRepository, Clock clock) {
        this.carRepository = carRepository;
        this.clock = clock;
    }

    public Car execute(@NotNull SaveCarRequest request) {
        LocalDateTime now = LocalDateTime.now(clock);
        Car car = Car.builder()
            .name(request.name())
                .brand(request.brand())
                .model(request.model())
                .year(request.year())
                .type(request.type())
                .maxCapacity(request.maxCapacity())
                .status(request.status())
                .gearboxType(request.gearboxType())
                .engineType(request.engineType())
                .amenities(request.amenities())
                .description(request.description())
                .imageUrl(request.imageUrl())
                .mileage(request.mileage())
                .fuelConsumption(request.fuelConsumption())
                .createdAt(now)
                .updatedAt(now)
            .build()
        ;

        return carRepository.save(car);
    }
}
