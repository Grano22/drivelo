package io.github.grano22.carfleetapp.carrental.actions;

import io.github.grano22.carfleetapp.carfleetmanagement.Car;
import io.github.grano22.carfleetapp.carfleetmanagement.domain.CarStatus;
import io.github.grano22.carfleetapp.carfleetmanagement.infrastructure.persistance.CarRepository;
import io.github.grano22.carfleetapp.carrental.CarRental;
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
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ReturnCarUseCaseTest {
    @Autowired
    private ReturnCarUseCase returnCarUseCase;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarRentalRepository carRentalRepository;

    @Autowired
    private CarRentalOfferRepository carRentalOfferRepository;

    @Autowired
    private AdjustableClock clock;

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
        carRepository.deleteAll();
        carRentalOfferRepository.deleteAll();
        carRentalRepository.deleteAll();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideVariousValidCases")
    public void customerCanReturnACar(
        String description,
        BigDecimal expectedFinalUserBalance,
        LocalDateTime rentedFrom,
        LocalDateTime rentedTo,
        LocalDateTime now
    ) {
        // Assert
        clock.set(now);
        User anUser = userRepository.save(UsersMother.bornCustomer());
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
        CarRental carRental = carRentalRepository.save(
            CarRental.builder()
                .user(anUser)
                .offer(carRentalOffer)
                .lockedPricePerDay(.19)
                .rentedFrom(rentedFrom)
                .rentedUntil(rentedTo)
                .build()
        );

        // Act
        returnCarUseCase.execute(anUser.getId(), carRental.getId());

        // Assert
        assertThat(carRentalRepository.findAll()).hasSize(0);
        assertThat(carRepository.findAll())
            .hasSize(1)
            .allSatisfy(car -> {
                assertThat(car.getStatus()).isEqualTo(CarStatus.AVAILABLE);
            })
        ;
        assertThat(userRepository.findAll())
            .hasSize(1)
            .allSatisfy(user -> {
                assertThat(user.getCredits()).isEqualByComparingTo(expectedFinalUserBalance);
            })
        ;
    }

    private static Stream<Arguments> provideVariousValidCases() {
        LocalDateTime now = LocalDateTime.of(2025, 9, 8, 0, 0);

        return Stream.of(
            Arguments.of(
            "Car returned in the middle of rental period and credits were returned partially for unused days",
                BigDecimal.valueOf(300.76),
                LocalDateTime.of(2025, 9, 6, 0, 0),
                LocalDateTime.of(2025, 9, 12, 0, 0),
                now
            ),
            Arguments.of(
                "Car returned before rental period and 100% credits were returned",
                BigDecimal.valueOf(301.14),
                LocalDateTime.of(2025, 9, 12, 0, 0),
                LocalDateTime.of(2025, 9, 18, 0, 0),
                 now
            ),
            Arguments.of(
                "Car returned after rental period and credits were consumed for additional days",
                BigDecimal.valueOf(299.62),
                LocalDateTime.of(2025, 9, 1, 0, 0),
                LocalDateTime.of(2025, 9, 6, 0, 0),
                now
            ),
            Arguments.of(
                "Car returned when client requested it in the same day as rental",
                BigDecimal.valueOf(300.19),
                LocalDateTime.of(2025, 9, 8, 8, 0),
                LocalDateTime.of(2025, 9, 9, 23, 59),
                LocalDateTime.of(2025, 9, 8, 22, 0)
            ),
            Arguments.of(
                "Car returned when client requested it in the next day as rental",
                BigDecimal.valueOf(300),
                LocalDateTime.of(2025, 9, 8, 8, 0),
                LocalDateTime.of(2025, 9, 9, 23, 59),
                LocalDateTime.of(2025, 9, 9, 0, 0)
            )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideInternalInvalidStateCases")
    public void customerCannotReturnACarDueToInvalidState(
        String description,
        LocalDateTime rentedFrom,
        LocalDateTime rentedTo,
        LocalDateTime now
    ) {
        // Assert
        clock.set(now);
        var fixtures = provideTypicalFixture(rentedFrom, rentedTo);

        // Act
        InvalidStateForOperation caughtProblem = assertThrows(InvalidStateForOperation.class, () -> {
            returnCarUseCase.execute(fixtures.anUser.getId(), fixtures.carRental.getId());
        });
        assertThat(caughtProblem.getMessage()).isEqualTo("Server error, invalid rental date range stored");
    }

    private static Stream<Arguments> provideInternalInvalidStateCases() {
        LocalDateTime now = LocalDateTime.of(2025, 9, 8, 0, 0);

        return Stream.of(
            Arguments.of(
                "Car returned when client requested it in the same day as rental",
                LocalDateTime.of(2025, 9, 8, 8, 0),
                LocalDateTime.of(2025, 9, 8, 23, 59),
                LocalDateTime.of(2025, 9, 8, 22, 0)
            ),
            Arguments.of(
                "Car returned when client requested it in the next day as rental",
                LocalDateTime.of(2025, 9, 8, 8, 0),
                LocalDateTime.of(2025, 9, 8, 23, 59),
                LocalDateTime.of(2025, 9, 9, 0, 0)
            )
        );
    }

    @Test
    public void carCannotBeReturnedIfUserHaveNotEnoughCreditsToPayProlongedRental() {
        // Assert
        LocalDateTime now = LocalDateTime.of(2025, 9, 8, 0, 0);
        clock.set(now);
        User anUser = userRepository.save(UsersMother.bornCustomer());
        Car aCar = carRepository.save(CarMother.bornToyotaSupraMk5());
        LocalDateTime rentedFrom = LocalDateTime.of(2025, 1, 6, 0, 0);
        LocalDateTime rentedTo = LocalDateTime.of(2025, 6, 12, 0, 0);

        CarRentalOffer carRentalOffer = carRentalOfferRepository.save(
            CarRentalOffer.builder()
                .car(aCar)
                .description("Only now, awesome sport car to rent")
                .minRentalDays(2)
                .maxRentalDays(20)
                .pricePerDay(150)
                .status(CarRentalOfferStatus.ACTIVE)
                .build()
        );
        CarRental carRental = carRentalRepository.save(
            CarRental.builder()
                .user(anUser)
                .offer(carRentalOffer)
                .lockedPricePerDay(200)
                .rentedFrom(rentedFrom)
                .rentedUntil(rentedTo)
                .build()
        );

        // Act & Assert
        InvalidStateForOperation caughtProblem = assertThrows(InvalidStateForOperation.class, () -> {
            returnCarUseCase.execute(anUser.getId(), carRental.getId());
        });
        assertThat(caughtProblem.getMessage()).isEqualTo("User f9415b6f-53b5-4fea-adea-4e11f4c0cb48 has insufficient credits");
    }

    @Test
    public void carCannotBeReturnedIfAccountNotFound() {
        // Assert
        LocalDateTime now = LocalDateTime.of(2025, 9, 8, 0, 0);
        clock.set(now);
        LocalDateTime rentedFrom = LocalDateTime.of(2025, 9, 6, 0, 0);
        LocalDateTime rentedTo = LocalDateTime.of(2025, 9, 12, 0, 0);
        var fixtures = provideTypicalFixture(rentedFrom, rentedTo);

        // Act & Assert
        InvalidDataGivenForOperation caughtProblem = assertThrows(InvalidDataGivenForOperation.class, () -> {
            returnCarUseCase.execute(UUID.fromString("84ba9692-5f94-4a4b-be32-f2374e7f2363"), fixtures.carRental.getId());
        });
        assertThat(caughtProblem.getMessage()).isEqualTo("User 84ba9692-5f94-4a4b-be32-f2374e7f2363 not found");
    }

    @Test
    public void carCannotBeReturnedIfRentalNotFound() {
        // Assert
        LocalDateTime now = LocalDateTime.of(2025, 9, 8, 0, 0);
        clock.set(now);
        LocalDateTime rentedFrom = LocalDateTime.of(2025, 9, 6, 0, 0);
        LocalDateTime rentedTo = LocalDateTime.of(2025, 9, 12, 0, 0);
        var fixtures = provideTypicalFixture(rentedFrom, rentedTo);

        // Act & Assert
        InvalidDataGivenForOperation caughtProblem = assertThrows(InvalidDataGivenForOperation.class, () -> {
            returnCarUseCase.execute(fixtures.anUser.getId(), UUID.fromString("d81d6a69-a75c-4fab-b1b3-f25483e53706"));
        });
        assertThat(caughtProblem.getMessage()).isEqualTo("Car rental d81d6a69-a75c-4fab-b1b3-f25483e53706 not found");
    }

    @Test
    public void carCannotBeReturnedForInActiveUser() {
        // Assert
        LocalDateTime now = LocalDateTime.of(2025, 9, 8, 0, 0);
        clock.set(now);
        User anUser = userRepository.save(UsersMother.bornInactiveCustomer());
        Car aCar = carRepository.save(CarMother.bornToyotaSupraMk5());
        LocalDateTime rentedFrom = LocalDateTime.of(2025, 9, 6, 0, 0);
        LocalDateTime rentedTo = LocalDateTime.of(2025, 9, 12, 0, 0);

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
        CarRental carRental = carRentalRepository.save(
            CarRental.builder()
                .user(anUser)
                .offer(carRentalOffer)
                .lockedPricePerDay(.19)
                .rentedFrom(rentedFrom)
                .rentedUntil(rentedTo)
                .build()
        );

        // Act & Assert
        InvalidStateForOperation caughtProblem = assertThrows(InvalidStateForOperation.class, () -> {
            returnCarUseCase.execute(anUser.getId(), carRental.getId());
        });
        assertThat(caughtProblem.getMessage()).isEqualTo("User f9415b6f-53b5-4fea-adea-4e11f4c0cb4a is not active");
    }

    private CaseFixtures provideTypicalFixture(LocalDateTime rentedFrom, LocalDateTime rentedTo) {
        User anUser = userRepository.save(UsersMother.bornCustomer());
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
        CarRental carRental = carRentalRepository.save(
            CarRental.builder()
                .user(anUser)
                .offer(carRentalOffer)
                .lockedPricePerDay(.19)
                .rentedFrom(rentedFrom)
                .rentedUntil(rentedTo)
                .build()
        );

        return new CaseFixtures(anUser, aCar, carRentalOffer, carRental);
    }

    private record CaseFixtures(User anUser, Car aCar, CarRentalOffer carRentalOffer, CarRental carRental) {};
}
