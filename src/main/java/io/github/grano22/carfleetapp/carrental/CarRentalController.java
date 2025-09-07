package io.github.grano22.carfleetapp.carrental;

import io.github.grano22.carfleetapp.carrental.actions.CarRenter;
import io.github.grano22.carfleetapp.carrental.actions.ReturnCarUseCase;
import io.github.grano22.carfleetapp.carrental.assembler.RentedCarsByUserAssembler;
import io.github.grano22.carfleetapp.carrental.contract.CarRentalRequest;
import io.github.grano22.carfleetapp.carrental.contract.CarReturnRequest;
import io.github.grano22.carfleetapp.carrental.contract.UserRentedCarView;
import io.github.grano22.carfleetapp.usermanagement.User;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/service/car-rental/v1")
public class CarRentalController {
    private final CarRenter carRenter;
    private final ReturnCarUseCase returnCarUseCase;
    private final RentedCarsByUserAssembler rentedCarsByUserAssembler;

    public CarRentalController(
        CarRenter carRenter,
        ReturnCarUseCase returnCarUseCase,
        RentedCarsByUserAssembler rentedCarsByUserAssembler
    ) {
        this.carRenter = carRenter;
        this.returnCarUseCase = returnCarUseCase;
        this.rentedCarsByUserAssembler = rentedCarsByUserAssembler;
    }

    @GetMapping("/rented")
    @PreAuthorize("hasAuthority(T(io.github.grano22.carfleetapp.usermanagement.domain.UserPermission).VIEW_RENTED_CARS.name())")
    public UserRentedCarView[] getRentedCars(@AuthenticationPrincipal User user) {
        return rentedCarsByUserAssembler.assemble(user.getId());
    }

    @PostMapping("/rent")
    @PreAuthorize("hasAuthority(T(io.github.grano22.carfleetapp.usermanagement.domain.UserPermission).RENT_CARS.name())")
    public void rentCar(@RequestBody @Valid CarRentalRequest request, @AuthenticationPrincipal User user) {
        carRenter.rent(user.getId(), request.offerId(), request.from(), request.to());
    }

    @PostMapping("/return")
    @PreAuthorize("hasAuthority(T(io.github.grano22.carfleetapp.usermanagement.domain.UserPermission).RETURN_CAR.name())")
    public void returnCar(@RequestBody @Valid CarReturnRequest request, @AuthenticationPrincipal User user) {
        returnCarUseCase.execute(user.getId(), request.rentalId());
    }
}
