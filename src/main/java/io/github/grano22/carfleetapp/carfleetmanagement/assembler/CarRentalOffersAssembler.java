package io.github.grano22.carfleetapp.carfleetmanagement.assembler;

import io.github.grano22.carfleetapp.carfleetmanagement.CarRentalOfferToViewMapper;
import io.github.grano22.carfleetapp.carfleetmanagement.contract.CarRentalOfferDetailsView;
import io.github.grano22.carfleetapp.carrental.infrastructure.persistance.CarRentalOfferRepository;
import org.springframework.stereotype.Service;

import java.util.stream.StreamSupport;

@Service
public class CarRentalOffersAssembler {
    private final CarRentalOfferRepository carRentalOfferRepository;

    public CarRentalOffersAssembler(CarRentalOfferRepository carRentalOfferRepository) {
        this.carRentalOfferRepository = carRentalOfferRepository;
    }

    public CarRentalOfferDetailsView[] assemble() {
        return StreamSupport.stream(carRentalOfferRepository.findAll().spliterator(), false)
            .map(CarRentalOfferToViewMapper::map)
            .toArray(CarRentalOfferDetailsView[]::new)
        ;
    }
}
