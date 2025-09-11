package io.github.grano22.carfleetapp.carfleetmanagement;

import io.github.grano22.carfleetapp.carfleetmanagement.contract.CarRentalOfferDetailsView;
import io.github.grano22.carfleetapp.carrental.CarRentalOffer;
import io.github.grano22.carfleetapp.shared.ViewFormatters;
import lombok.NonNull;

public class CarRentalOfferToViewMapper {
    public static @NonNull CarRentalOfferDetailsView map(@NonNull CarRentalOffer carRentalOffer) {
        return new CarRentalOfferDetailsView(
            carRentalOffer.getCar().getId().toString(),
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
        );
    }
}
