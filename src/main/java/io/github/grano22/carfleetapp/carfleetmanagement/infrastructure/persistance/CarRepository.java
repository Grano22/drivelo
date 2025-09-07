package io.github.grano22.carfleetapp.carfleetmanagement.infrastructure.persistance;

import io.github.grano22.carfleetapp.carfleetmanagement.Car;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface CarRepository extends CrudRepository<Car, UUID> {
}
