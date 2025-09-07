package io.github.grano22.carfleetapp.carrental.infrastructure.persistance;

import io.github.grano22.carfleetapp.carfleetmanagement.domain.CarStatus;
import io.github.grano22.carfleetapp.carrental.CarRentalOffer;
import io.github.grano22.carfleetapp.carrental.domain.CarRentalOfferStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.UUID;

public interface CarRentalOfferRepository extends CrudRepository<CarRentalOffer, UUID> {
    Collection<CarRentalOffer> findAllByStatusAndCar_Status(CarRentalOfferStatus status, CarStatus carStatus);
}
