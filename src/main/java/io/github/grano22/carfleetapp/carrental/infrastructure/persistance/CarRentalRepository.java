package io.github.grano22.carfleetapp.carrental.infrastructure.persistance;

import io.github.grano22.carfleetapp.carrental.CarRental;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.UUID;

public interface CarRentalRepository extends CrudRepository<CarRental, UUID> {
    public Collection<CarRental> findAllByUser_Id(UUID userId);
}
