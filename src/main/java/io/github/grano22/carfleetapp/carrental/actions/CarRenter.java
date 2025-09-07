package io.github.grano22.carfleetapp.carrental.actions;

import io.github.grano22.carfleetapp.carfleetmanagement.domain.CarStatus;
import io.github.grano22.carfleetapp.carfleetmanagement.infrastructure.persistance.CarRepository;
import io.github.grano22.carfleetapp.carrental.CarRental;
import io.github.grano22.carfleetapp.carrental.CarRentalOffer;
import io.github.grano22.carfleetapp.shared.domain.InvalidDataGivenForOperation;
import io.github.grano22.carfleetapp.usermanagement.infrastructure.persistance.UserRepository;
import io.github.grano22.carfleetapp.carrental.domain.CarRentalOfferStatus;
import io.github.grano22.carfleetapp.carrental.infrastructure.persistance.CarRentalOfferRepository;
import io.github.grano22.carfleetapp.carrental.infrastructure.persistance.CarRentalRepository;
import io.github.grano22.carfleetapp.usermanagement.User;
import io.github.grano22.carfleetapp.usermanagement.domain.UserStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class CarRenter {
    private final UserRepository userRepository;
    private final CarRentalOfferRepository carRentalOfferRepository;
    private final CarRentalRepository carRentalRepository;
    private final CarRepository carRepository;
    private final Clock clock;

    public CarRenter(
        UserRepository userRepository,
        CarRentalOfferRepository carRentalOfferRepository,
        CarRentalRepository carRentalRepository,
        CarRepository carRepository,
        Clock clock
    ) {
        this.userRepository = userRepository;
        this.carRentalOfferRepository = carRentalOfferRepository;
        this.carRentalRepository = carRentalRepository;
        this.carRepository = carRepository;
        this.clock = clock;
    }

    @Transactional
    public CarRental rent(UUID userId, UUID offerId, LocalDateTime from, LocalDateTime to) {
        User rentee = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User " + userId + " not found"))
        ;
        CarRentalOffer offer = carRentalOfferRepository.findById(offerId)
            .orElseThrow(() -> new IllegalArgumentException("Car rental offer " + offerId + " not found"))
        ;

        if (!rentee.getStatus().equals(UserStatus.ACTIVE)) {
            throw new IllegalStateException("User " + userId + " is not active");
        }

        if (!offer.getStatus().equals(CarRentalOfferStatus.ACTIVE)) {
            throw new IllegalStateException("Offer " + offerId + " is no longer active");
        }

        if (offer.getCar().getStatus() != CarStatus.AVAILABLE) {
            throw new IllegalStateException("Car " + offer.getCar().getId() + " is not available");
        }

        long howManyDays = ChronoUnit.DAYS.between(from, to);

        if (
            (offer.getMinRentalDays() != null && howManyDays < offer.getMinRentalDays()) ||
            (offer.getMaxRentalDays() != null && howManyDays > offer.getMaxRentalDays())
        ) {
            throw new InvalidDataGivenForOperation("Offer " + offerId + " does not allow rental for " + howManyDays + " days");
        }

        double finalPotentialPrice = offer.getPricePerDay() * howManyDays;

        if (finalPotentialPrice > rentee.getCredits().doubleValue()) {
            throw new IllegalStateException("User " + userId + " has insufficient credits");
        }

        double balance = rentee.getCredits().doubleValue() - finalPotentialPrice;
        rentee = rentee.toBuilder().credits(BigDecimal.valueOf(balance)).build();

        userRepository.save(rentee);
        carRepository.save(offer.getCar().toBuilder().status(CarStatus.RENTED).build());

        LocalDateTime rentalStart = LocalDateTime.now(clock);
        CarRental rental = CarRental.builder()
            .user(rentee)
            .offer(offer)
            .lockedPricePerDay(offer.getPricePerDay())
            .rentedFrom(rentalStart)
            .rentedUntil(rentalStart.plusDays(howManyDays))
            .build()
        ;

        return carRentalRepository.save(rental);
    }
}
