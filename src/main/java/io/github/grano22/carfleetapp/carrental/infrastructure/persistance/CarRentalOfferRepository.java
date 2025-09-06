package io.github.grano22.carfleetapp.carrental.infrastructure.persistance;

import io.github.grano22.carfleetapp.carrental.CarRentalOffer;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface CarRentalOfferRepository extends CrudRepository<CarRentalOffer, UUID> {
}
