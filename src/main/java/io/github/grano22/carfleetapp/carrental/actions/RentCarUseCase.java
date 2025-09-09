package io.github.grano22.carfleetapp.carrental.actions;

import io.github.grano22.carfleetapp.carfleetmanagement.domain.CarStatus;
import io.github.grano22.carfleetapp.carfleetmanagement.infrastructure.persistance.CarRepository;
import io.github.grano22.carfleetapp.carrental.CarRental;
import io.github.grano22.carfleetapp.carrental.CarRentalOffer;
import io.github.grano22.carfleetapp.shared.domain.InvalidDataGivenForOperation;
import io.github.grano22.carfleetapp.shared.domain.InvalidStateForOperation;
import io.github.grano22.carfleetapp.usermanagement.infrastructure.persistance.UserRepository;
import io.github.grano22.carfleetapp.carrental.domain.CarRentalOfferStatus;
import io.github.grano22.carfleetapp.carrental.infrastructure.persistance.CarRentalOfferRepository;
import io.github.grano22.carfleetapp.carrental.infrastructure.persistance.CarRentalRepository;
import io.github.grano22.carfleetapp.usermanagement.User;
import io.github.grano22.carfleetapp.usermanagement.domain.UserStatus;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class RentCarUseCase {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserRepository userRepository;
    private final CarRentalOfferRepository carRentalOfferRepository;
    private final CarRentalRepository carRentalRepository;
    private final CarRepository carRepository;
    private final Clock clock;

    public RentCarUseCase(
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
    public CarRental execute(
        @NonNull UUID userId,
        @NonNull UUID offerId,
        @NonNull LocalDateTime from,
        @NonNull LocalDateTime to
    ) {
        User rentee = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User " + userId + " not found"))
        ;
        CarRentalOffer offer = carRentalOfferRepository.findById(offerId)
            .orElseThrow(() -> new IllegalArgumentException("Car rental offer " + offerId + " not found"))
        ;

        if (!rentee.getStatus().equals(UserStatus.ACTIVE)) {
            throw new InvalidStateForOperation("User " + userId + " is not active");
        }

        if (!offer.getStatus().equals(CarRentalOfferStatus.ACTIVE)) {
            throw new InvalidStateForOperation("Offer " + offerId + " is no longer active");
        }

        if (offer.getCar().getStatus() != CarStatus.AVAILABLE) {
            throw new InvalidStateForOperation("Car " + offer.getCar().getId() + " is not available");
        }

        LocalDateTime today = LocalDateTime.now(clock).toLocalDate().atStartOfDay();

        if (to.isBefore(from)) {
            throw new InvalidDataGivenForOperation("Rental from date must be before rental end date");
        }

        if (from.isBefore(today)) {
            throw new InvalidDataGivenForOperation("Rental date range must be at least in the same day as now");
        }

        LocalDate fromDay = from.toLocalDate();
        LocalDate toDay = to.toLocalDate();

        long howManyDays = ChronoUnit.DAYS.between(fromDay, toDay);

        if (howManyDays <= 0) {
            throw new InvalidDataGivenForOperation("User must rent at least for one day");
        }

        if (
            (offer.getMinRentalDays() != null && howManyDays < offer.getMinRentalDays()) ||
            (offer.getMaxRentalDays() != null && howManyDays > offer.getMaxRentalDays())
        ) {
            throw new InvalidDataGivenForOperation("This offer does not allow rental for " + howManyDays + " day" + (howManyDays > 1 ? "s" : ""));
        }

        BigDecimal finalPotentialPrice = BigDecimal.valueOf(offer.getPricePerDay()).multiply(BigDecimal.valueOf(howManyDays));

        if (finalPotentialPrice.compareTo(rentee.getCredits()) > 0) {
            throw new InvalidStateForOperation("User " + userId + " has insufficient credits");
        }

        BigDecimal balance = rentee.getCredits().subtract(finalPotentialPrice);
        rentee = rentee.toBuilder().credits(balance).build();

        userRepository.save(rentee);
        carRepository.save(offer.getCar().toBuilder().status(CarStatus.RENTED).build());

        CarRental rental = CarRental.builder()
            .user(rentee)
            .offer(offer)
            .lockedPricePerDay(offer.getPricePerDay())
            .rentedFrom(from)
            .rentedUntil(to)
            .build()
        ;

        logger.info(
            "[RentCarUseCase] User {} has rented car from offer {} from {} to {} and has balance {}",
            userId,
            offer.getId(),
            from,
            to,
            balance.doubleValue()
        );

        return carRentalRepository.save(rental);
    }
}
