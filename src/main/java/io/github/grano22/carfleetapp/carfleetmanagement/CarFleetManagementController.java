package io.github.grano22.carfleetapp.carfleetmanagement;

import io.github.grano22.carfleetapp.carfleetmanagement.actions.AddCarWithRentalOfferUseCase;
import io.github.grano22.carfleetapp.carfleetmanagement.actions.UpdateCarWithRentalOfferUseCase;
import io.github.grano22.carfleetapp.carfleetmanagement.contract.SaveCarWithRentalOfferRequest;
import io.github.grano22.carfleetapp.shared.validation.OnCreate;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/service/car-fleet-management/v1")
public class CarFleetManagementController {
    private final AddCarWithRentalOfferUseCase addCarWithRentalOfferUseCase;
    private final UpdateCarWithRentalOfferUseCase updateCarWithRentalOfferUseCase;

    public CarFleetManagementController(AddCarWithRentalOfferUseCase addCarWithRentalOfferUseCase, UpdateCarWithRentalOfferUseCase updateCarWithRentalOfferUseCase) {
        this.addCarWithRentalOfferUseCase = addCarWithRentalOfferUseCase;
        this.updateCarWithRentalOfferUseCase = updateCarWithRentalOfferUseCase;
    }

    @PostMapping("/car-with-rental-offer/save/")
    @PreAuthorize("hasAuthority(T(io.github.grano22.carfleetapp.usermanagement.domain.UserPermission).ADD_CAR_RENTAL_OFFERS.name())")
    public void saveCarOffer(@RequestBody @Validated(OnCreate.class) SaveCarWithRentalOfferRequest request) {
        addCarWithRentalOfferUseCase.execute(request);
    }

    @PutMapping("/car-with-rental-offer/save/{carId}")
    @PreAuthorize("hasAuthority(T(io.github.grano22.carfleetapp.usermanagement.domain.UserPermission).MODIFY_CAR_RENTAL_OFFERS.name())")
    public void saveCarOffer(@PathVariable("carId") UUID carId, @RequestBody @Valid SaveCarWithRentalOfferRequest request) {
        updateCarWithRentalOfferUseCase.execute(carId, request);
    }
}
