package io.github.grano22.carfleetapp.carfleetmanagement.actions;

import io.github.grano22.carfleetapp.carfleetmanagement.Car;
import io.github.grano22.carfleetapp.carfleetmanagement.contract.SaveCarWithRentalOfferRequest;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddCarWithRentalOfferUseCase {
    private final AddCarUseCase addCarUseCase;
    private final CreateCarRentalOfferUseCase createCarRentalOfferUseCase;

    public AddCarWithRentalOfferUseCase(AddCarUseCase addCarUseCase, CreateCarRentalOfferUseCase createCarRentalOfferUseCase) {
        this.addCarUseCase = addCarUseCase;
        this.createCarRentalOfferUseCase = createCarRentalOfferUseCase;
    }

    @Transactional
    public void execute(@NonNull SaveCarWithRentalOfferRequest request) {
        Car createdCar = addCarUseCase.execute(request.car());
        createCarRentalOfferUseCase.execute(createdCar.getId(), request.rentalOfferDetails());
    }
}
