package io.github.grano22.carfleetapp.carfleetmanagement;

import io.github.grano22.carfleetapp.carfleetmanagement.domain.CarAmenity;
import io.github.grano22.carfleetapp.carfleetmanagement.domain.CarType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
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

    private String name;
    private String brand;
    private String model;
    private int year;

    @Enumerated(EnumType.STRING)
    private CarType type;

    @Column(name = "max_capacity", nullable = false)
    private int maxCapacity;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "gearbox_type", nullable = false)
    private String gearboxType;

    @Column(name = "engine_type", nullable = false)
    private String engineType;

    @ElementCollection(targetClass = CarAmenity.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "car_amenities", joinColumns = @JoinColumn(name = "car_amenity_id"))
    @Column(nullable = false)
    private List<CarAmenity> amenities;

    private String description;
    private String imageUrl;
    private int mileage;
    private double fuelConsumption;

    public Car() {}
}
