package io.github.grano22.carfleetapp.carfleetmanagement;

import io.github.grano22.carfleetapp.carfleetmanagement.assembler.CarRentalOffersAssembler;
import io.github.grano22.carfleetapp.carfleetmanagement.contract.CarRentalOfferDetailsView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service/car-fleet/v1")
public class CarFleetDetailsController {
    private final CarRentalOffersAssembler carRentalOffersAssembler;

    public CarFleetDetailsController(CarRentalOffersAssembler carRentalOffersAssembler) {
        this.carRentalOffersAssembler = carRentalOffersAssembler;
    }

    @GetMapping
    public CarRentalOfferDetailsView[] getCarFleetOffers() {
        return carRentalOffersAssembler.assemble();
    }
}
