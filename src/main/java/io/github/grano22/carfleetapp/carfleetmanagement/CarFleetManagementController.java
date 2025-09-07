package io.github.grano22.carfleetapp.carfleetmanagement;

import io.github.grano22.carfleetapp.carfleetmanagement.actions.AddCarUseCase;
import io.github.grano22.carfleetapp.carfleetmanagement.actions.AddCarWithRentalOfferUseCase;
import io.github.grano22.carfleetapp.carfleetmanagement.actions.UpdateCarUseCase;
import io.github.grano22.carfleetapp.carfleetmanagement.actions.UpdateCarWithRentalOfferUseCase;
import io.github.grano22.carfleetapp.carfleetmanagement.contract.SaveCarRequest;
import io.github.grano22.carfleetapp.carfleetmanagement.contract.SaveCarWithRentalOfferRequest;
import io.github.grano22.carfleetapp.shared.validation.OnCreate;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/service/car-fleet-management/v1")
public class CarFleetManagementController {
    private final AddCarUseCase addCarUseCase;
    private final UpdateCarUseCase updateCarUseCase;
    private final AddCarWithRentalOfferUseCase addCarWithRentalOfferUseCase;
    private final UpdateCarWithRentalOfferUseCase updateCarWithRentalOfferUseCase;

    public CarFleetManagementController(AddCarUseCase addCarUseCase, UpdateCarUseCase updateCarUseCase, AddCarWithRentalOfferUseCase addCarWithRentalOfferUseCase, UpdateCarWithRentalOfferUseCase updateCarWithRentalOfferUseCase) {
        this.addCarUseCase = addCarUseCase;
        this.updateCarUseCase = updateCarUseCase;
        this.addCarWithRentalOfferUseCase = addCarWithRentalOfferUseCase;
        this.updateCarWithRentalOfferUseCase = updateCarWithRentalOfferUseCase;
    }

    @PostMapping("/car/save")
    public void addCar(@RequestBody @Validated(OnCreate.class) SaveCarRequest request) {
        addCarUseCase.execute(request);
    }

    @PutMapping("/car/save/{id}")
    public void updateCar(@PathVariable("id") UUID carId, @RequestBody @Valid SaveCarRequest request) {
        updateCarUseCase.execute(carId, request);
    }

    @PostMapping("/car-with-rental-offer/save/")
    public void saveCarOffer(@RequestBody @Validated(OnCreate.class) SaveCarWithRentalOfferRequest request) {
        addCarWithRentalOfferUseCase.execute(request);
    }

    @PutMapping("/car-with-rental-offer/save/{carId}")
    public void saveCarOffer(@PathVariable("carId") UUID carId, @RequestBody @Valid SaveCarWithRentalOfferRequest request) {
        updateCarWithRentalOfferUseCase.execute(carId, request);
    }
}
