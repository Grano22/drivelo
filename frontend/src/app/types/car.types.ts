import {z} from 'zod';
import {CarRentalOfferStatus} from "./car-rental-offer.types";

export enum VehicleType {
    SUV = 'SUV',
    MINIVAN = 'MINIVAN',
    SEDAN = 'SEDAN',
    HATCHBACK = 'HATCHBACK',
    COUPE = 'COUPE',
    CONVERTIBLE = 'CONVERTIBLE',
    LIMOUSINE = 'LIMOUSINE'
}

export enum GearboxType {
    MANUAL = 'MANUAL',
    SEMI_AUTO = 'SEMI_AUTO',
    AUTOMATIC = 'AUTOMATIC',
}

export enum EngineType {
    DIESEL = 'DIESEL',
    GASOLINE = 'GASOLINE',
    HYBRID = 'HYBRID',
    ELECTRIC = 'HYBRID',
    LPG = 'LPG',
    FUEL_CELL_PLUGIN = 'FUEL_CELL_PLUGIN'
}

export enum CarStatus {
    AVAILABLE = 'AVAILABLE',
    RENTED = 'RENTED',
    MAINTENANCE = 'MAINTENANCE',
    OUT_OF_SERVICE = 'OUT_OF_SERVICE',
}

export enum AmenityType {
    AIR_CONDITIONING = 'AIR_CONDITIONING',
    AUDIO_7_1_SYSTEM = 'AUDIO_7_1_SYSTEM',
    BIG_TRUNK = 'BIG_TRUNK',
    ANDROID_AUTO = 'ANDROID_AUTO',
    APPLE_CARPLAY = 'APPLE_CARPLAY',
    HEATED_SEATS = 'HEATED_SEATS',
    SUNROOF = 'SUNROOF',
    GPS = 'GPS',
    BLUETOOTH = 'BLUETOOTH',
    USB_PORTS = 'USB_PORTS',
}

export const AmenitySchema = z.object({
    type: z.enum(AmenityType),
    name: z.string(),
    icon: z.string(),
});

export const CarSchema = z.object({
    brand: z.string().min(1, 'Brand is required'),
    model: z.string().min(1, 'Model is required'),
    year: z.number().min(1900).max(new Date().getFullYear() + 1),
    vehicleType: z.enum(VehicleType),
    maxCapacity: z.number().min(1).max(20),
    gearboxType: z.enum(GearboxType),
    engineType: z.enum(EngineType),
    amenities: z.array(z.enum(AmenityType)),
    carStatus: z.enum(CarStatus),
    carDescription: z.string(),
    imageUrl: z.string().url().nullable().optional(),
    mileage: z.number().min(0),
    fuelConsumption: z.number().min(0)
});

export const CarRentalOfferSchema = CarSchema.extend({
    id: z.uuid(),
    pricePerDay: z.number().min(0, 'Price must be non-negative'),
    status: z.enum(CarRentalOfferStatus),
    description: z.string(),
    minRentalDays: z.number().min(1).nullable().optional(),
    maxRentalDays: z.number().min(1).nullable().optional(),
    createdAt: z.iso.datetime(),
    updatedAt: z.iso.datetime(),
});

export const CarRentalSchema = CarSchema.extend({
    id: z.uuid(),
    dailyFee: z.number().min(0, 'Daily fee must be non-negative'),
    rentedFrom: z.iso.datetime(),
    rentedUntil: z.iso.datetime()
});

export const RentalRequestSchema = z.object({
    carId: z.uuid(),
    userId: z.uuid(),
    startDate: z.iso.datetime(),
    endDate: z.iso.datetime(),
    totalDays: z.number().min(1),
    totalPrice: z.number().min(0),
});

export type CarRentalOffer = z.infer<typeof CarRentalOfferSchema>;
export type CarRental = z.infer<typeof CarRentalSchema>;

export type Amenity = z.infer<typeof AmenitySchema>;
export type RentalRequest = z.infer<typeof RentalRequestSchema>;