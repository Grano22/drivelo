package io.github.grano22.carfleetapp.config;

import io.github.grano22.carfleetapp.carfleetmanagement.Car;
import io.github.grano22.carfleetapp.carfleetmanagement.domain.CarStatus;
import io.github.grano22.carfleetapp.carfleetmanagement.infrastructure.persistance.CarRepository;
import io.github.grano22.carfleetapp.carrental.CarRental;
import io.github.grano22.carfleetapp.carrental.CarRentalOffer;
import io.github.grano22.carfleetapp.carrental.domain.CarRentalOfferStatus;
import io.github.grano22.carfleetapp.carrental.infrastructure.persistance.CarRentalOfferRepository;
import io.github.grano22.carfleetapp.carrental.infrastructure.persistance.CarRentalRepository;
import io.github.grano22.carfleetapp.kit.InMemoryCrudRepository;
import io.github.grano22.carfleetapp.usermanagement.User;
import io.github.grano22.carfleetapp.usermanagement.domain.UserRole;
import io.github.grano22.carfleetapp.usermanagement.domain.UserStatus;
import io.github.grano22.carfleetapp.usermanagement.infrastructure.persistance.UserRepository;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

@Configuration(proxyBeanMethods = false)
@ActiveProfiles("test")
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class UnitTestRepositories {
    public static class InMemoryUserRepository extends InMemoryCrudRepository<User, UUID> implements UserRepository {
        public InMemoryUserRepository() {
            super(User.class, UUID.class);
        }

        @Override
        public Optional<User> findByEmail(String email) {
            return findBy(Map.of("email", email));
        }

        @Override
        public Collection<User> findByRoles(Set<UserRole> roles) {
            return (Collection<User>)findAllBy(Map.of("roles", roles));
        }
    }

    public static class InMemoryCarRepository extends InMemoryCrudRepository<Car, UUID> implements CarRepository {
        public InMemoryCarRepository() {
            super(Car.class, UUID.class);
        }
    }

    public static class InMemoryCarRentalOfferRepository extends InMemoryCrudRepository<CarRentalOffer, UUID> implements CarRentalOfferRepository {
        public InMemoryCarRentalOfferRepository() {
            super(CarRentalOffer.class, UUID.class);
        }

        @Override
        public Collection<CarRentalOffer> findAllByStatusAndCar_Status(CarRentalOfferStatus status, CarStatus carStatus) {
            return findBy(Map.of("status", status))
                .stream()
                .filter(carRentalOffer -> carRentalOffer.getCar()
                .getStatus()
                .equals(carStatus))
                .toList()
            ;
        }
    }

    public static class InMemoryCarRentalRepository extends InMemoryCrudRepository<CarRental, UUID> implements CarRentalRepository {
        public InMemoryCarRentalRepository() {
            super(CarRental.class, UUID.class);
        }
    }

    @Primary
    @Bean
    public UserRepository userRepository() {
        return new InMemoryUserRepository();
    }

    @Primary
    @Bean
    public CarRepository carRepository() {
        return new InMemoryCarRepository();
    }

    @Primary
    @Bean
    public CarRentalOfferRepository carRentalOfferRepository() {
        return new InMemoryCarRentalOfferRepository();
    }

    @Primary
    @Bean
    public CarRentalRepository carRentalRepository() {
        return new InMemoryCarRentalRepository();
    }
}
