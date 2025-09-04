import { z } from 'zod';

export enum UserRole {
  CUSTOMER = 'cars_rental_customer',
  MANAGER = 'cars_manager',
}

export enum UserStatus {
  ACTIVE = 'active',
  INACTIVE = 'inactive',
  BLOCKED = 'blocked',
}

export const AddressSchema = z.object({
  street: z.string().min(1, 'Street is required'),
  zipCode: z.string().regex(/^\d{2}-\d{3}$/, 'Invalid zip code format'),
  city: z.string().min(1, 'City is required'),
  voivodeship: z.string().min(1, 'Voivodeship is required'),
  country: z.string().min(1, 'Country is required'),
});

export const UserSchema = z.object({
  id: z.uuid(),
  firstName: z.string().min(1, 'First name is required'),
  lastName: z.string().min(1, 'Last name is required'),
  email: z.email('Invalid email format'),
  phone: z.string().regex(/^\+?\d{9,15}$/, 'Invalid phone number'),
  birthDate: z.string().regex(/^\d{4}-\d{2}-\d{2}$/, 'Invalid date format'),
  address: AddressSchema,
  credits: z.number().min(0, 'Credits must be non-negative'),
  status: z.enum(UserStatus),
  roles: z.array(z.enum(UserRole)),
  createdAt: z.iso.datetime(),
  updatedAt: z.iso.datetime(),
});

export const CreateUserSchema = UserSchema.omit({ 
  id: true, 
  createdAt: true, 
  updatedAt: true 
});

export type User = z.infer<typeof UserSchema>;
export type CreateUser = z.infer<typeof CreateUserSchema>;
export type Address = z.infer<typeof AddressSchema>;