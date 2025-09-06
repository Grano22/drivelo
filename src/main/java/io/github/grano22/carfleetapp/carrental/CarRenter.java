package io.github.grano22.carfleetapp.carrental;

import io.github.grano22.carfleetapp.usermanagement.infrastructure.persistance.UserRepository;
import io.github.grano22.carfleetapp.carrental.domain.CarRentalOfferStatus;
import io.github.grano22.carfleetapp.carrental.infrastructure.persistance.CarRentalOfferRepository;
import io.github.grano22.carfleetapp.carrental.infrastructure.persistance.CarRentalRepository;
import io.github.grano22.carfleetapp.usermanagement.User;
import io.github.grano22.carfleetapp.usermanagement.domain.UserStatus;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CarRenter {
    private final UserRepository userRepository;
    private final CarRentalOfferRepository carRentalOfferRepository;
    private final CarRentalRepository carRentalRepository;
    private final Clock clock;

    public CarRenter(
        UserRepository userRepository,
        CarRentalOfferRepository carRentalOfferRepository,
        CarRentalRepository carRentalRepository,
        Clock clock
    ) {
        this.userRepository = userRepository;
        this.carRentalOfferRepository = carRentalOfferRepository;
        this.carRentalRepository = carRentalRepository;
        this.clock = clock;
    }

    public CarRental rent(UUID userId, UUID offerId, int howManyDays) {
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
