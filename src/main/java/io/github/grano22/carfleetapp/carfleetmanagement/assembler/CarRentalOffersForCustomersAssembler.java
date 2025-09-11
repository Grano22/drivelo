package io.github.grano22.carfleetapp.carfleetmanagement.assembler;

import io.github.grano22.carfleetapp.carfleetmanagement.CarRentalOfferToViewMapper;
import io.github.grano22.carfleetapp.carfleetmanagement.contract.CarRentalOfferDetailsView;
import io.github.grano22.carfleetapp.carfleetmanagement.domain.CarStatus;
import io.github.grano22.carfleetapp.carrental.domain.CarRentalOfferStatus;
import io.github.grano22.carfleetapp.carrental.infrastructure.persistance.CarRentalOfferRepository;
import org.springframework.stereotype.Service;

@Service
public class CarRentalOffersForCustomersAssembler {
    private final CarRentalOfferRepository carRentalOfferRepository;

    public CarRentalOffersForCustomersAssembler(CarRentalOfferRepository carRentalOfferRepository) {
        this.carRentalOfferRepository = carRentalOfferRepository;
    }

    public CarRentalOfferDetailsView[] assemble() {
        return carRentalOfferRepository.findAllByStatusAndCar_Status(CarRentalOfferStatus.ACTIVE, CarStatus.AVAILABLE)
            .stream()
            .map(CarRentalOfferToViewMapper::map)
            .toArray(CarRentalOfferDetailsView[]::new)
        ;
    }
}
