import {inject, Injectable, signal} from '@angular/core';
import {Router} from '@angular/router';
import {AppStore} from '../store/app.store';
import {User, UserRole} from '../types/user.types';
import {AuthInternalHttpClient} from "./auth-internal-http-client";
import {switchMap} from "rxjs";
import {UserDetailsInternalHttpClient} from "./user-details-internal-http-client";

@Injectable({providedIn: 'root'})
export class AuthService {
    readonly #authInternalHttpClient = inject(AuthInternalHttpClient);
    readonly #userDetailsInternalHttpClient = inject(UserDetailsInternalHttpClient);
    readonly #router = inject(Router);
    readonly #store = inject(AppStore);

    readonly isAuthenticated = signal<boolean>(false);

    constructor() {
    }

    login(email: string, password: string): Promise<boolean> {
        return new Promise((resolve) => {
            this.#authInternalHttpClient.login(email, password).pipe(
                switchMap(_ => {
                    return this.#userDetailsInternalHttpClient.getCurrentUserDetails();
                })
            ).subscribe({
                next: (response) => {
                    this.#store.setUser(response);
                    this.isAuthenticated.set(true);
                    localStorage.setItem('lastLoggedIn', new Date().toISOString());
                    resolve(true);
                },
                error: (err) => {
                    console.error(err);
                    resolve(false);
                }
            });
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
        const lastLoggedAt = localStorage.getItem('lastLoggedIn');
        console.log(lastLoggedAt);
        console.log(lastLoggedAt && new Date(lastLoggedAt).getTime() + 1000 * 60 * 60 * 24 * 7, new Date().getTime());

        if (lastLoggedAt && new Date(lastLoggedAt).getTime() + 1000 * 60 * 60 * 24 * 7 < new Date().getTime()) {
            return;
        }

        console.log('Auto login');
        this.#userDetailsInternalHttpClient.getCurrentUserDetails().subscribe({
            next: (user) => {
                this.#store.setUser(user);
                this.isAuthenticated.set(true);
                this.#router.navigate(['/dashboard']);
            },
            error: (err) => {
                console.error(err);
                localStorage.removeItem('currentUser');
            }
        });
    }
}