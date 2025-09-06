package io.github.grano22.carfleetapp.carrental;

import io.github.grano22.carfleetapp.usermanagement.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "car_rentals"
)
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class CarRental {
    @Id
    private UUID id;

    @ManyToOne
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private CarRentalOffer offer;

    @Column(name = "locked_price_per_day", nullable = false)
    private double lockedPricePerDay;

    @Column(name = "rented_from", nullable = false)
    private LocalDateTime rentedFrom;

    @Column(name = "rented_until", nullable = false)
    private LocalDateTime rentedUntil;

    public CarRental() {}
}
