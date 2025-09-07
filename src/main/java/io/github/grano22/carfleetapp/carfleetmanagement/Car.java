package io.github.grano22.carfleetapp.carfleetmanagement;

import io.github.grano22.carfleetapp.carfleetmanagement.domain.*;
import io.github.grano22.carfleetapp.carrental.CarRentalOffer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "cars")
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String brand;

    @Column
    private String model;

    @Column
    private int year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarType type;

    @Column(name = "max_capacity", nullable = false)
    private int maxCapacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CarStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "gearbox_type", nullable = false)
    private GearboxType gearboxType;

    @Enumerated(EnumType.STRING)
    @Column(name = "engine_type", nullable = false)
    private EngineType engineType;

    @ElementCollection(targetClass = CarAmenity.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "car_amenities", joinColumns = @JoinColumn(name = "car_amenity_id"))
    @Column(nullable = false)
    private Set<CarAmenity> amenities;

    @Column(nullable = false)
    private String description;

    @Column
    private String imageUrl;

    @Column
    private int mileage;

    @Column(name = "fuel_consumption")
    private double fuelConsumption;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    public Car() {}

    @OneToOne(mappedBy = "car")
    private CarRentalOffer rentalOffer;
}
