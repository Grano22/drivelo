package io.github.grano22.carfleetapp.carrental;

import io.github.grano22.carfleetapp.carfleetmanagement.Car;
import io.github.grano22.carfleetapp.carrental.domain.CarRentalOfferStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "car_rental_offers")
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class CarRentalOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    private Car car;

    @Column(name = "price_per_day", nullable = false)
    private double pricePerDay;

    @Column(name = "min_rental_days")
    private Integer minRentalDays;

    @Column(name = "max_rental_days")
    private Integer maxRentalDays;

    @Column(name = "stauts")
    @Enumerated(EnumType.STRING)
    private CarRentalOfferStatus status;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String description;

    public CarRentalOffer() {}
}
