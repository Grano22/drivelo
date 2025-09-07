package io.github.grano22.carfleetapp.carfleetmanagement.actions;

import io.github.grano22.carfleetapp.carfleetmanagement.contract.SaveCarWithRentalOfferRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdateCarWithRentalOfferUseCase {
    private final UpdateCarUseCase updateCarUseCase;
    private final UpdateCarRentalOfferUseCase updateCarRentalOfferUseCase;

    public UpdateCarWithRentalOfferUseCase(UpdateCarUseCase updateCarUseCase, UpdateCarRentalOfferUseCase updateCarRentalOfferUseCase) {
        this.updateCarUseCase = updateCarUseCase;
        this.updateCarRentalOfferUseCase = updateCarRentalOfferUseCase;
    }

    @Transactional
    public void execute(UUID carId, SaveCarWithRentalOfferRequest request) {
        updateCarUseCase.execute(carId, request.car());
        updateCarRentalOfferUseCase.execute(carId, request.rentalOfferDetails());
    }
}
