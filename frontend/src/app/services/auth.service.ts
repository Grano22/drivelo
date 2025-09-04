import { Injectable, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { MockDataService } from './mock-data.service';
import { AppStore } from '../store/app.store';
import { User, UserRole } from '../types/user.types';

@Injectable({ providedIn: 'root' })
export class AuthService {
  readonly #mockDataService = inject(MockDataService);
  readonly #router = inject(Router);
  readonly #store = inject(AppStore);

  readonly isAuthenticated = signal<boolean>(false);

  login(email: string, password: string): Promise<boolean> {
    return new Promise((resolve) => {
      setTimeout(() => {
        const users = this.#mockDataService.getUsers();
        const user = users.find(u => u.email === email);
        
        if (user && password === 'password123') {
          this.#store.setUser(user);
          this.isAuthenticated.set(true);
          resolve(true);
        } else {
          resolve(false);
        }
      }, 1000);
    });
  }

  logout(): void {
    this.#store.setUser(null);
    this.isAuthenticated.set(false);
    this.#router.navigate(['/login']);
  }

  hasPermission(permission: UserRole): boolean {
    const user = this.#store.currentUser();
    return user?.roles.includes(permission) || false;
  }

  checkAutoLogin(): void {
    const savedUser = localStorage.getItem('currentUser');
    if (savedUser) {
      try {
        const user = JSON.parse(savedUser) as User;
        this.#store.setUser(user);
        this.isAuthenticated.set(true);
      } catch {
        localStorage.removeItem('currentUser');
      }
    }
  }
}