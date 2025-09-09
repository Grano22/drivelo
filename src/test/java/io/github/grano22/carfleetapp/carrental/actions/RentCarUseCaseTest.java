package io.github.grano22.carfleetapp.carrental.actions;

import io.github.grano22.carfleetapp.carfleetmanagement.Car;
import io.github.grano22.carfleetapp.carfleetmanagement.domain.CarStatus;
import io.github.grano22.carfleetapp.carfleetmanagement.infrastructure.persistance.CarRepository;
import io.github.grano22.carfleetapp.carrental.CarRentalOffer;
import io.github.grano22.carfleetapp.carrental.domain.CarRentalOfferStatus;
import io.github.grano22.carfleetapp.carrental.infrastructure.persistance.CarRentalOfferRepository;
import io.github.grano22.carfleetapp.carrental.infrastructure.persistance.CarRentalRepository;
import io.github.grano22.carfleetapp.kit.AdjustableClock;
import io.github.grano22.carfleetapp.kit.CarMother;
import io.github.grano22.carfleetapp.kit.UsersMother;
import io.github.grano22.carfleetapp.shared.domain.InvalidDataGivenForOperation;
import io.github.grano22.carfleetapp.shared.domain.InvalidStateForOperation;
import io.github.grano22.carfleetapp.usermanagement.User;
import io.github.grano22.carfleetapp.usermanagement.infrastructure.persistance.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class RentCarUseCaseTest {
    @Autowired
    private RentCarUseCase rentCarUseCase;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarRentalOfferRepository carRentalOfferRepository;

    @Autowired
    private CarRentalRepository carRentalRepository;

    @Autowired
    private AdjustableClock clock;

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
        carRepository.deleteAll();
        carRentalOfferRepository.deleteAll();
        carRentalRepository.deleteAll();
    }

    @Test
    public void testUserRentedACarWhenHasAvailableFundsAndPickedCorrectDateFromTodayPlus4Days() {
        // Assert
        LocalDateTime now = LocalDateTime.of(2025, 9, 8, 0, 0);
        clock.set(now);
        User userWithFunds = UsersMother.bornCustomer();
        userRepository.save(userWithFunds);

        Car aCar = carRepository.save(CarMother.bornToyotaSupraMk5());
        CarRentalOffer carRentalOffer = carRentalOfferRepository.save(
            CarRentalOffer.builder()
                .car(aCar)
                .description("Only now, awesome sport car to rent")
                .minRentalDays(2)
                .maxRentalDays(20)
                .pricePerDay(0.3)
                .status(CarRentalOfferStatus.ACTIVE)
                .build()
        );

        LocalDateTime rentUntil = LocalDateTime.of(2025, 9, 12, 0, 0);

        // Act
        rentCarUseCase.execute(userWithFunds.getId(), carRentalOffer.getId(), now, rentUntil);

        // Assert
        assertThat(carRentalRepository.findAll())
            .hasSize(1)
            .allSatisfy(rental -> {
                assertThat(rental.getUser().getId()).isEqualTo(userWithFunds.getId());
                assertThat(rental.getOffer().getId()).isEqualTo(carRentalOffer.getId());
                assertThat(rental.getLockedPricePerDay()).isEqualTo(.3);
                assertThat(rental.getRentedFrom()).isEqualTo(now);
                assertThat(rental.getRentedUntil()).isEqualTo(rentUntil);
            })
        ;
        assertThat(carRepository.findAll())
            .hasSize(1)
            .allSatisfy(car -> {
                assertThat(car.getStatus()).isEqualTo(CarStatus.RENTED);
            })
        ;
        assertThat(userRepository.findAll())
            .hasSize(1)
            .allSatisfy(user -> {
                assertThat(user.getCredits()).isEqualTo(BigDecimal.valueOf(298.8));
            })
        ;
    }

    @Test
    public void testUserRentedACarWhenHasAvailableFundsAndPickedCorrectDateFromFuturePlus10Days() {
        // Assert
        LocalDateTime now = LocalDateTime.of(2025, 9, 8, 0, 0);
        clock.set(now);
        User userWithFunds = UsersMother.bornCustomer().toBuilder().credits(BigDecimal.valueOf(100000D)).build();
        userRepository.save(userWithFunds);

        Car aCar = carRepository.save(CarMother.bornToyotaSupraMk5());
        CarRentalOffer carRentalOffer = carRentalOfferRepository.save(
            CarRentalOffer.builder()
                .car(aCar)
                .description("Only now, awesome sport car to rent")
                .minRentalDays(2)
                .maxRentalDays(20)
                .pricePerDay(93.33)
                .status(CarRentalOfferStatus.ACTIVE)
                .build()
        );

        LocalDateTime rentFrom = LocalDateTime.of(2025, 9, 12, 0, 0);
        LocalDateTime rentUntil = LocalDateTime.of(2025, 9, 22, 0, 0);

        // Act
        rentCarUseCase.execute(userWithFunds.getId(), carRentalOffer.getId(), rentFrom, rentUntil);

        // Assert
        assertThat(carRentalRepository.findAll())
            .hasSize(1)
            .allSatisfy(rental -> {
                assertThat(rental.getUser().getId()).isEqualTo(userWithFunds.getId());
                assertThat(rental.getOffer().getId()).isEqualTo(carRentalOffer.getId());
                assertThat(rental.getLockedPricePerDay()).isEqualTo(93.33);
                assertThat(rental.getRentedFrom()).isEqualTo(rentFrom);
                assertThat(rental.getRentedUntil()).isEqualTo(rentUntil);
            })
        ;
        assertThat(carRepository.findAll())
            .hasSize(1)
            .allSatisfy(car -> {
                assertThat(car.getStatus()).isEqualTo(CarStatus.RENTED);
            })
        ;
        assertThat(userRepository.findAll())
            .hasSize(1)
            .allSatisfy(user -> {
                assertThat(user.getCredits()).isEqualByComparingTo(BigDecimal.valueOf(99066.7));
            })
        ;
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideInvalidRentalDates")
    public void testUserRentedACarFromThePastAndThatShouldNotBeAccepted(
        String description,
        String expectedError,
        LocalDateTime from,
        LocalDateTime to
    ) {
        // Arrange
        LocalDateTime now = LocalDateTime.of(2025, 9, 8, 0, 0);
        clock.set(now);
        User userWithFunds = UsersMother.bornCustomer();
        userRepository.save(userWithFunds);

        Car aCar = carRepository.save(CarMother.bornToyotaSupraMk5());
        CarRentalOffer carRentalOffer = carRentalOfferRepository.save(
            CarRentalOffer.builder()
                .car(aCar)
                .description("Only now, awesome sport car to rent")
                .minRentalDays(2)
                .maxRentalDays(20)
                .pricePerDay(9)
                .status(CarRentalOfferStatus.ACTIVE)
                .build()
        );

        // Act & Assert
        InvalidDataGivenForOperation problem = assertThrows(InvalidDataGivenForOperation.class, () -> {
            rentCarUseCase.execute(userWithFunds.getId(), carRentalOffer.getId(), from, to);
        });
        assertThat(problem.getMessage()).isEqualTo(expectedError);
    }

    private static Stream<Arguments> provideInvalidRentalDates() {
        return Stream.of(
            Arguments.of(
                "Rental range is before now",
                "Rental date range must be at least in the same day as now",
                LocalDateTime.of(2025, 9, 1, 0, 0),
                LocalDateTime.of(2025, 9, 7, 0, 0)
            ),
            Arguments.of(
                "Rental start date is before now",
                "Rental date range must be at least in the same day as now",
                LocalDateTime.of(2025, 9, 5, 0, 0),
                LocalDateTime.of(2025, 9, 9, 0, 0)
            ),
            Arguments.of(
                "Rental from is after rental to",
                "Rental from date must be before rental end date",
                LocalDateTime.of(2025, 9, 15, 0, 0),
                LocalDateTime.of(2025, 9, 13, 0, 0)
            ),
            Arguments.of(
                "Cannot rent for 0 days",
                "User must rent at least for one day",
                LocalDateTime.of(2025, 9, 15, 0, 0),
                LocalDateTime.of(2025, 9, 15, 0, 0)
            ),
            Arguments.of(
                "Cannot rent for 1 day, because offer requires at least 2 days",
                "This offer does not allow rental for 1 day",
                LocalDateTime.of(2025, 9, 20, 0, 0),
                LocalDateTime.of(2025, 9, 21, 0, 0)
            ),
            Arguments.of(
                "Cannot rent for 21 days, because offer requires below 20 days",
                "This offer does not allow rental for 21 days",
                LocalDateTime.of(2025, 9, 9, 0, 0),
                LocalDateTime.of(2025, 9, 30, 0, 0)
            )
        );
    }

    @Test
    public void userCannotSendRentalRequestBecauseHeIsNoLongerActive() {
        // Arrange
        LocalDateTime now = LocalDateTime.of(2025, 9, 8, 0, 0);
        clock.set(now);
        User userWithFunds = UsersMother.bornBlockedCustomer();
        userRepository.save(userWithFunds);

        Car aCar = carRepository.save(CarMother.bornToyotaSupraMk5());
        CarRentalOffer carRentalOffer = carRentalOfferRepository.save(
            CarRentalOffer.builder()
                .car(aCar)
                .description("Only now, awesome sport car to rent")
                .minRentalDays(2)
                .maxRentalDays(20)
                .pricePerDay(9)
                .status(CarRentalOfferStatus.ACTIVE)
                .build()
        );

        LocalDateTime rentFrom = LocalDateTime.of(2025, 9, 12, 0, 0);
        LocalDateTime rentUntil = LocalDateTime.of(2025, 9, 22, 0, 0);

        // Act & Assert
        InvalidStateForOperation problem = assertThrows(InvalidStateForOperation.class, () -> {
            rentCarUseCase.execute(userWithFunds.getId(), carRentalOffer.getId(), rentFrom, rentUntil);
        });
        assertThat(problem.getMessage()).isEqualTo("User f9415b6f-53b5-4fea-adea-4e11f4c0cb4b is not active");
    }

    @Test
    public void carRentalCannotBeRequestedWhenUserHasInsufficientCreditsAmount() {
        // Arrange
        LocalDateTime now = LocalDateTime.of(2025, 9, 8, 0, 0);
        clock.set(now);
        User userWithFunds = UsersMother.bornCustomer();
        userRepository.save(userWithFunds);

        Car aCar = carRepository.save(CarMother.bornToyotaSupraMk5());
        CarRentalOffer carRentalOffer = carRentalOfferRepository.save(
            CarRentalOffer.builder()
                .car(aCar)
                .description("Only now, awesome sport car to rent")
                .minRentalDays(2)
                .maxRentalDays(20)
                .pricePerDay(555)
                .status(CarRentalOfferStatus.ACTIVE)
                .build()
        );

        LocalDateTime rentFrom = LocalDateTime.of(2025, 9, 12, 0, 0);
        LocalDateTime rentUntil = LocalDateTime.of(2025, 9, 16, 0, 0);

        // Act & Assert
        InvalidStateForOperation problem = assertThrows(InvalidStateForOperation.class, () -> {
            rentCarUseCase.execute(userWithFunds.getId(), carRentalOffer.getId(), rentFrom, rentUntil);
        });
        assertThat(problem.getMessage()).isEqualTo("User f9415b6f-53b5-4fea-adea-4e11f4c0cb48 has insufficient credits");
    }
}
