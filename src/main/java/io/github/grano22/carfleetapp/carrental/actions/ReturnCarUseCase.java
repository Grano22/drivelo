package io.github.grano22.carfleetapp.carrental.actions;

import io.github.grano22.carfleetapp.carfleetmanagement.domain.CarStatus;
import io.github.grano22.carfleetapp.carfleetmanagement.infrastructure.persistance.CarRepository;
import io.github.grano22.carfleetapp.carrental.CarRental;
import io.github.grano22.carfleetapp.carrental.infrastructure.persistance.CarRentalRepository;
import io.github.grano22.carfleetapp.shared.domain.InvalidDataGivenForOperation;
import io.github.grano22.carfleetapp.shared.domain.InvalidStateForOperation;
import io.github.grano22.carfleetapp.usermanagement.User;
import io.github.grano22.carfleetapp.usermanagement.domain.UserStatus;
import io.github.grano22.carfleetapp.usermanagement.infrastructure.persistance.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class ReturnCarUseCase {
    private final CarRentalRepository carRentalRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final Clock clock;

    public ReturnCarUseCase(
        CarRentalRepository carRentalRepository,
        UserRepository userRepository,
        CarRepository carRepository,
        Clock clock
    ) {
        this.carRentalRepository = carRentalRepository;
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.clock = clock;
    }

    @Transactional
    public void execute(UUID userId, UUID rentalId) {
        User rentee = userRepository.findById(userId)
            .orElseThrow(() -> new InvalidDataGivenForOperation("User " + userId + " not found"))
        ;
        CarRental rental = carRentalRepository.findById(rentalId)
            .orElseThrow(() -> new InvalidDataGivenForOperation("Car rental " + rentalId + " not found"))
        ;

        if (!rentee.getStatus().equals(UserStatus.ACTIVE)) {
            throw new InvalidStateForOperation("User " + userId + " is not active");
        }

        LocalDate today = LocalDateTime.now(clock).toLocalDate();
        LocalDate fromDay = rental.getRentedFrom().toLocalDate();
        LocalDate untilDay = rental.getRentedUntil().toLocalDate();

        if (fromDay.equals(untilDay)) {
            throw new InvalidStateForOperation("Server error, invalid rental date range stored");
        }

        long rentingDays = ChronoUnit.DAYS.between(fromDay, today);
        long expectedRentingDays = ChronoUnit.DAYS.between(fromDay, untilDay);
        BigDecimal finalPrice = BigDecimal.valueOf(rental.getLockedPricePerDay()).multiply(BigDecimal.valueOf(rentingDays));
        BigDecimal intendedPrice = BigDecimal.valueOf(rental.getLockedPricePerDay()).multiply(BigDecimal.valueOf(expectedRentingDays));

        if (fromDay.isAfter(today)) {
            BigDecimal balance = rentee.getCredits().add(intendedPrice);
            rentee = rentee.toBuilder().credits(balance).build();
            finalizeReturn(rentee, rental);

            return;
        }

        if (finalPrice.compareTo(intendedPrice) < 0) {
            BigDecimal balance = rentee.getCredits().add(intendedPrice.subtract(finalPrice));

            rentee = rentee.toBuilder().credits(balance).build();
        }

        if (finalPrice.compareTo(intendedPrice) > 0) {
            BigDecimal balance = rentee.getCredits().subtract(finalPrice.subtract(intendedPrice));

            if (balance.signum() == -1) {
                throw new InvalidStateForOperation("User " + userId + " has insufficient credits");
            }

            rentee = rentee.toBuilder().credits(balance).build();
        }

        finalizeReturn(rentee, rental);
    }

    private void finalizeReturn(User rentee, CarRental rental) {
        if (rental.getOffer().getCar().getStatus().equals(CarStatus.RENTED)) {
            carRepository.save(rental.getOffer().getCar().toBuilder().status(CarStatus.AVAILABLE).build());
        }

        userRepository.save(rentee);
        carRentalRepository.delete(rental);
    }
}

