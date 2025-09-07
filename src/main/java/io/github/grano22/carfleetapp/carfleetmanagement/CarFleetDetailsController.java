package io.github.grano22.carfleetapp.carfleetmanagement;

import io.github.grano22.carfleetapp.carfleetmanagement.assembler.CarRentalOffersAssembler;
import io.github.grano22.carfleetapp.carfleetmanagement.assembler.CarRentalOffersForCustomersAssembler;
import io.github.grano22.carfleetapp.carfleetmanagement.contract.CarRentalOfferDetailsView;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service/car-fleet/v1")
public class CarFleetDetailsController {
    private final CarRentalOffersAssembler carRentalOffersAssembler;
    private final CarRentalOffersForCustomersAssembler carRentalOffersForCustomersAssembler;

    public CarFleetDetailsController(CarRentalOffersAssembler carRentalOffersAssembler, CarRentalOffersForCustomersAssembler carRentalOffersForCustomersAssembler) {
        this.carRentalOffersAssembler = carRentalOffersAssembler;
        this.carRentalOffersForCustomersAssembler = carRentalOffersForCustomersAssembler;
    }

    @GetMapping
    @PreAuthorize("hasAuthority(T(io.github.grano22.carfleetapp.usermanagement.domain.UserPermission).VIEW_CAR_RENTAL_OFFERS.name())")
    public CarRentalOfferDetailsView[] getCarFleetOffers() {
        return carRentalOffersAssembler.assemble();
    }

    @GetMapping("/for_rent")
    @PreAuthorize("hasAuthority(T(io.github.grano22.carfleetapp.usermanagement.domain.UserPermission).VIEW_AVAILABLE_CAR_OFFERS.name())")
    public CarRentalOfferDetailsView[] getCarsForRent() {
        return carRentalOffersForCustomersAssembler.assemble();
    }
}
