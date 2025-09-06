import { z } from 'zod';

export enum VehicleType {
  SUV = 'suv',
  MINIVAN = 'minivan',
  SEDAN = 'sedan',
  HATCHBACK = 'hatchback',
  COUPE = 'coupe',
  CONVERTIBLE = 'convertible',
  TRUCK = 'truck',
}

export enum GearboxType {
  MANUAL = 'manual',
  SEMI_AUTO = 'semi_auto',
  AUTOMATIC = 'automatic',
}

export enum EngineType {
  PETROL = 'petrol',
  DIESEL = 'diesel',
  HYBRID = 'hybrid',
  ELECTRIC = 'electric',
}

export enum CarStatus {
  AVAILABLE = 'available',
  RENTED = 'rented',
  MAINTENANCE = 'maintenance',
  OUT_OF_SERVICE = 'out_of_service',
}

export enum AmenityType {
  AIR_CONDITIONING = 'air_conditioning',
  AUDIO_7_1_SYSTEM = 'audio_7_1_system',
  BIG_TRUNK = 'big_trunk',
  ANDROID_AUTO = 'android_auto',
  APPLE_CARPLAY = 'apple_carplay',
  HEATED_SEATS = 'heated_seats',
  SUNROOF = 'sunroof',
  GPS = 'gps',
  BLUETOOTH = 'bluetooth',
  USB_PORTS = 'usb_ports',
}

export const AmenitySchema = z.object({
  type: z.enum(AmenityType),
  name: z.string(),
  icon: z.string(),
});

export const CarSchema = z.object({
  id: z.uuid(),
  brand: z.string().min(1, 'Brand is required'),
  model: z.string().min(1, 'Model is required'),
  year: z.number().min(1900).max(new Date().getFullYear() + 1),
  vehicleType: z.enum(VehicleType),
  maxCapacity: z.number().min(1).max(20),
  pricePerDay: z.number().min(0, 'Price must be non-negative'),
  status: z.enum(CarStatus),
  gearboxType: z.enum(GearboxType),
  engineType: z.enum(EngineType),
  amenities: z.array(z.enum(AmenityType)),
  description: z.string().optional(),
  imageUrl: z.string().url().optional(),
  mileage: z.number().min(0),
  fuelConsumption: z.number().min(0),
  maxRentalDays: z.number().min(1),
  createdAt: z.iso.datetime(),
  updatedAt: z.iso.datetime(),
});

export const CreateCarSchema = CarSchema.omit({ 
  id: true, 
  createdAt: true, 
  updatedAt: true 
});

export const RentalRequestSchema = z.object({
  carId: z.uuid(),
  userId: z.uuid(),
  startDate: z.iso.datetime(),
  endDate: z.iso.datetime(),
  totalDays: z.number().min(1),
  totalPrice: z.number().min(0),
});

export type Car = z.infer<typeof CarSchema>;
export type CreateCar = z.infer<typeof CreateCarSchema>;
export type Amenity = z.infer<typeof AmenitySchema>;
export type RentalRequest = z.infer<typeof RentalRequestSchema>;