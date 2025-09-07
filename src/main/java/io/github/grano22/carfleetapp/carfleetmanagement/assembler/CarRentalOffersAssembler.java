package io.github.grano22.carfleetapp.carfleetmanagement.assembler;

import io.github.grano22.carfleetapp.carfleetmanagement.contract.CarRentalOfferDetailsView;
import io.github.grano22.carfleetapp.carrental.infrastructure.persistance.CarRentalOfferRepository;
import io.github.grano22.carfleetapp.shared.ViewFormatters;
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
            .map(carRentalOffer -> new CarRentalOfferDetailsView(
                carRentalOffer.getId().toString(),
                carRentalOffer.getCar().getBrand(),
                carRentalOffer.getCar().getModel(),
                carRentalOffer.getCar().getYear(),
                carRentalOffer.getCar().getType().name(),
                carRentalOffer.getCar().getMaxCapacity(),
                carRentalOffer.getPricePerDay(),
                carRentalOffer.getStatus().name(),
                carRentalOffer.getCar().getStatus().name(),
                carRentalOffer.getCar().getGearboxType().name(),
                carRentalOffer.getCar().getEngineType().name(),
                carRentalOffer.getCar().getAmenities().stream().map(Enum::name).toList(),
                carRentalOffer.getDescription(),
                carRentalOffer.getCar().getDescription(),
                carRentalOffer.getCar().getImageUrl(),
                carRentalOffer.getCar().getMileage(),
                carRentalOffer.getCar().getFuelConsumption(),
                carRentalOffer.getMinRentalDays(),
                carRentalOffer.getMinRentalDays(),
                ViewFormatters.formatDateTime(carRentalOffer.getCreatedAt()),
                ViewFormatters.formatDateTime(carRentalOffer.getUpdatedAt())
            ))
            .toArray(CarRentalOfferDetailsView[]::new)
        ;
    }
}
