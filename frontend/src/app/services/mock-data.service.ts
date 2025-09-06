import { Injectable } from '@angular/core';
import { User, UserRole, UserStatus } from '../types/user.types';
import { Car, VehicleType, GearboxType, EngineType, CarStatus, AmenityType } from '../types/car.types';

@Injectable({ providedIn: 'root' })
export class MockDataService {
  #mockCars: Car[] = [
    {
      id: 'car-1',
      brand: 'Tesla',
      model: 'Model S',
      year: 2024,
      vehicleType: VehicleType.SEDAN,
      maxCapacity: 5,
      pricePerDay: 120,
      status: CarStatus.AVAILABLE,
      gearboxType: GearboxType.AUTOMATIC,
      engineType: EngineType.ELECTRIC,
      amenities: [
        AmenityType.AIR_CONDITIONING,
        AmenityType.AUDIO_7_1_SYSTEM,
        AmenityType.ANDROID_AUTO,
        AmenityType.APPLE_CARPLAY,
        AmenityType.HEATED_SEATS,
        AmenityType.SUNROOF,
      ],
      description: 'Premium electric sedan with autopilot capabilities',
      imageUrl: 'https://images.pexels.com/photos/3764984/pexels-photo-3764984.jpeg',
      mileage: 15000,
      fuelConsumption: 0,
      maxRentalDays: 30,
      createdAt: '2024-01-01T00:00:00Z',
      updatedAt: '2024-12-01T00:00:00Z',
    },
    {
      id: 'car-2',
      brand: 'BMW',
      model: 'X5',
      year: 2023,
      vehicleType: VehicleType.SUV,
      maxCapacity: 7,
      pricePerDay: 95,
      status: CarStatus.AVAILABLE,
      gearboxType: GearboxType.AUTOMATIC,
      engineType: EngineType.DIESEL,
      amenities: [
        AmenityType.AIR_CONDITIONING,
        AmenityType.AUDIO_7_1_SYSTEM,
        AmenityType.BIG_TRUNK,
        AmenityType.HEATED_SEATS,
        AmenityType.GPS,
      ],
      description: 'Spacious luxury SUV perfect for family trips',
      imageUrl: 'https://images.pexels.com/photos/3764984/pexels-photo-3764984.jpeg',
      mileage: 25000,
      fuelConsumption: 8.5,
      maxRentalDays: 21,
      createdAt: '2024-02-01T00:00:00Z',
      updatedAt: '2024-12-01T00:00:00Z',
    },
    {
      id: 'car-3',
      brand: 'Volkswagen',
      model: 'Golf',
      year: 2023,
      vehicleType: VehicleType.HATCHBACK,
      maxCapacity: 5,
      pricePerDay: 45,
      status: CarStatus.AVAILABLE,
      gearboxType: GearboxType.MANUAL,
      engineType: EngineType.PETROL,
      amenities: [
        AmenityType.AIR_CONDITIONING,
        AmenityType.BLUETOOTH,
        AmenityType.USB_PORTS,
      ],
      description: 'Reliable and economical car for city driving',
      imageUrl: 'https://images.pexels.com/photos/3764984/pexels-photo-3764984.jpeg',
      mileage: 35000,
      fuelConsumption: 6.2,
      maxRentalDays: 14,
      createdAt: '2024-03-01T00:00:00Z',
      updatedAt: '2024-12-01T00:00:00Z',
    },
  ];

  getCars(): Car[] {
    return structuredClone(this.#mockCars);
  }

  getCarById(id: string): Car | null {
    return structuredClone(this.#mockCars.find(car => car.id === id) || null);
  }
}