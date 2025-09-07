package io.github.grano22.carfleetapp.carrental.actions;

import io.github.grano22.carfleetapp.carfleetmanagement.domain.CarStatus;
import io.github.grano22.carfleetapp.carfleetmanagement.infrastructure.persistance.CarRepository;
import io.github.grano22.carfleetapp.carrental.CarRental;
import io.github.grano22.carfleetapp.carrental.infrastructure.persistance.CarRentalOfferRepository;
import io.github.grano22.carfleetapp.carrental.infrastructure.persistance.CarRentalRepository;
import io.github.grano22.carfleetapp.usermanagement.User;
import io.github.grano22.carfleetapp.usermanagement.domain.UserStatus;
import io.github.grano22.carfleetapp.usermanagement.infrastructure.persistance.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class ReturnCarUseCase {
    private final CarRentalRepository carRentalRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;

    public ReturnCarUseCase(
        CarRentalRepository carRentalRepository,
        UserRepository userRepository,
        CarRepository carRepository
    ) {
        this.carRentalRepository = carRentalRepository;
        this.userRepository = userRepository;
        this.carRepository = carRepository;
    }

    @Transactional
    public void execute(UUID userId, UUID rentalId) {
        User rentee = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User " + userId + " not found"))
        ;
        CarRental rental = carRentalRepository.findById(rentalId)
            .orElseThrow(() -> new IllegalArgumentException("Car rental " + rentalId + " not found"))
        ;

        if (!rentee.getStatus().equals(UserStatus.ACTIVE)) {
            throw new IllegalStateException("User " + userId + " is not active");
        }

        LocalDateTime now = LocalDateTime.now();
        long rentingDays = ChronoUnit.DAYS.between(rental.getRentedFrom(), now);
        long expectedRentingDays = ChronoUnit.DAYS.between(rental.getRentedFrom(), rental.getRentedUntil());
        double finalPrice = rental.getLockedPricePerDay() * rentingDays;
        double intendedPrice = expectedRentingDays * rental.getLockedPricePerDay();

        if (finalPrice < intendedPrice) {
            double balance = rentee.getCredits().doubleValue() + (intendedPrice - finalPrice);

            rentee = rentee.toBuilder().credits(BigDecimal.valueOf(balance)).build();
        }

        if (finalPrice > intendedPrice) {
            double balance = rentee.getCredits().doubleValue() - (finalPrice - intendedPrice);

            rentee = rentee.toBuilder().credits(BigDecimal.valueOf(balance)).build();
        }

        if (rental.getOffer().getCar().getStatus().equals(CarStatus.RENTED)) {
            carRepository.save(rental.getOffer().getCar().toBuilder().status(CarStatus.AVAILABLE).build());
        }

        userRepository.save(rentee);
        carRentalRepository.delete(rental);
    }
}

