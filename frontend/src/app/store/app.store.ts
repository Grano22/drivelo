import { computed, inject } from '@angular/core';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { User, UserRole } from '../types/user.types';
import { Car } from '../types/car.types';

export interface AppState {
  currentUser: User | null;
  cars: Car[];
  users: User[];
  loading: boolean;
  error: string | null;
  theme: 'light' | 'dark';
  language: 'en-GB' | 'en-US' | 'pl-PL';
}

const initialState: AppState = {
  currentUser: null,
  cars: [],
  users: [],
  loading: false,
  error: null,
  theme: 'light',
  language: 'en-GB',
};

export const AppStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
    withComputed(({ currentUser, cars }) => ({
        isAuthenticated: computed<boolean>(() => !!currentUser()),
        isCustomer: computed<boolean>(() => currentUser()?.roles.includes(UserRole.CUSTOMER) || false),
        isManager: computed<boolean>(() => currentUser()?.roles.includes(UserRole.MANAGER) || false),
        availableCars: computed<Car[]>(() => cars().filter(car => car.status === 'available')),
    })),

    withMethods((store) => ({
    setUser: (user: User | null) => {
      patchState(store, { currentUser: user });
    },
    setCars: (cars: Car[]) => {
      patchState(store, { cars });
    },
    addCar: (car: Car) => {
      patchState(store, (state) => ({
        cars: [...state.cars, car]
      }));
    },
    updateCar: (carId: string, updates: Partial<Car>) => {
      patchState(store, (state) => ({
        cars: state.cars.map(car => 
          car.id === carId ? { ...car, ...updates } : car
        )
      }));
    },
    setUsers: (users: User[]) => {
      patchState(store, { users });
    },
    setLoading: (loading: boolean) => {
      patchState(store, { loading });
    },
    setError: (error: string | null) => {
      patchState(store, { error });
    },
    setTheme: (theme: 'light' | 'dark') => {
      patchState(store, { theme });
      localStorage.setItem('theme', theme);
      document.documentElement.classList.toggle('dark', theme === 'dark');
    },
    setLanguage: (language: 'en-GB' | 'en-US' | 'pl-PL') => {
      patchState(store, { language });
      localStorage.setItem('language', language);
    },
    initializeTheme: () => {
      const savedTheme = localStorage.getItem('theme') as 'light' | 'dark' | null;
      const theme = savedTheme || (window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light');
      patchState(store, { theme });
      document.documentElement.classList.toggle('dark', theme === 'dark');
    },
    initializeLanguage: () => {
      const savedLanguage = localStorage.getItem('language') as 'en-GB' | 'en-US' | 'pl-PL' | null;
      const language = savedLanguage || 'en-GB';
      patchState(store, { language });
    },
  }))
);