package io.github.grano22.carfleetapp.carfleetmanagement.assembler;

import io.github.grano22.carfleetapp.carfleetmanagement.contract.CarRentalOfferDetailsView;
import io.github.grano22.carfleetapp.carfleetmanagement.domain.CarStatus;
import io.github.grano22.carfleetapp.carrental.domain.CarRentalOfferStatus;
import io.github.grano22.carfleetapp.carrental.infrastructure.persistance.CarRentalOfferRepository;
import io.github.grano22.carfleetapp.shared.ViewFormatters;
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
