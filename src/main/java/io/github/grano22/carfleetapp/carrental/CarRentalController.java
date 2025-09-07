package io.github.grano22.carfleetapp.carrental;

import io.github.grano22.carfleetapp.carrental.actions.CarRenter;
import io.github.grano22.carfleetapp.carrental.actions.ReturnCarUseCase;
import io.github.grano22.carfleetapp.carrental.contract.CarRentalRequest;
import io.github.grano22.carfleetapp.carrental.contract.CarReturnRequest;
import io.github.grano22.carfleetapp.usermanagement.User;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service/car-rental/v1")
public class CarRentalController {
    private final CarRenter carRenter;
    private final ReturnCarUseCase returnCarUseCase;

    public CarRentalController(CarRenter carRenter, ReturnCarUseCase returnCarUseCase) {
        this.carRenter = carRenter;
        this.returnCarUseCase = returnCarUseCase;
    }

    @PostMapping("/rent")
    public void rentCar(@RequestBody @Valid CarRentalRequest request, @AuthenticationPrincipal User user) {
        carRenter.rent(user.getId(), request.offerId(), request.from(), request.to());
    }

    @PostMapping("/return")
    public void returnCar(@RequestBody @Valid CarReturnRequest request, @AuthenticationPrincipal User user) {
        returnCarUseCase.execute(user.getId(), request.rentalId());
    }
}
