import { z } from 'zod';

export enum UserRole {
  ADMIN = 'ADMIN',
  CUSTOMER = 'CUSTOMER',
  MANAGER = 'MANAGER',
}

export enum UserPermission {
  ADD_CUSTOMERS = 'ADD_CUSTOMERS',
  VIEW_CUSTOMERS = 'VIEW_CUSTOMERS',
  VIEW_OFFERS = 'VIEW_OFFERS',
  RENT_CARS = 'RENT_CARS',
}

export enum UserStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  BLOCKED = 'BLOCKED',
}

export const UserDetailsSchema = z.object({
    firstName: z.string().min(1, 'First name is required'),
    lastName: z.string().min(1, 'Last name is required'),
    email: z.email('Invalid email format'),
    phone: z.string(),
    birthDate: z.iso.date(),
    credits: z.number().min(0, 'Credits must be non-negative'),
    status: z.enum(UserStatus),
    createdAt: z.iso.datetime(),
    updatedAt: z.iso.datetime(),
});

export const CustomersSchema = z.array(UserDetailsSchema);

export const UserSchema = UserDetailsSchema.extend({
    roles: z.array(z.enum(UserRole)),
    permissions: z.array(z.enum(UserPermission))
});

export const CreateUserSchema = UserSchema.omit({ 
  id: true, 
  createdAt: true, 
  updatedAt: true 
});

export type UserDetails = z.infer<typeof UserDetailsSchema>;
export type User = z.infer<typeof UserSchema>;
export type CreateUser = z.infer<typeof CreateUserSchema>;

export type Customers = z.infer<typeof CustomersSchema>;