package io.github.grano22.carfleetapp.carrental.assembler;

import io.github.grano22.carfleetapp.carrental.contract.UserRentedCarView;
import io.github.grano22.carfleetapp.carrental.infrastructure.persistance.CarRentalRepository;
import io.github.grano22.carfleetapp.shared.ViewFormatters;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RentedCarsByUserAssembler {
    private final CarRentalRepository carRentalRepository;

    public RentedCarsByUserAssembler(CarRentalRepository carRentalRepository) {
        this.carRentalRepository = carRentalRepository;
    }

    public UserRentedCarView[] assemble(UUID userId) {
        return carRentalRepository.findAllByUser_Id(userId)
            .stream()
            .map(carRental -> new UserRentedCarView(
                carRental.getId().toString(),
                carRental.getOffer().getCar().getBrand(),
                carRental.getOffer().getCar().getModel(),
                carRental.getOffer().getCar().getYear(),
                carRental.getOffer().getCar().getType().name(),
                carRental.getOffer().getCar().getMaxCapacity(),
                carRental.getOffer().getCar().getStatus().name(),
                carRental.getOffer().getCar().getGearboxType().name(),
                carRental.getOffer().getCar().getEngineType().name(),
                carRental.getOffer().getCar().getAmenities().stream().map(Enum::name).toList(),
                carRental.getOffer().getCar().getDescription(),
                carRental.getOffer().getCar().getImageUrl(),
                carRental.getOffer().getCar().getMileage(),
                carRental.getOffer().getCar().getFuelConsumption(),
                ViewFormatters.formatDateTime(carRental.getRentedFrom()),
                ViewFormatters.formatDateTime(carRental.getRentedUntil()),
                carRental.getLockedPricePerDay(),
                ViewFormatters.formatDateTime(carRental.getOffer().getCar().getCreatedAt()),
                ViewFormatters.formatDateTime(carRental.getOffer().getCar().getUpdatedAt())
            ))
            .toArray(UserRentedCarView[]::new)
        ;
    }
}
