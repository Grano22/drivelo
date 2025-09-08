import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, NonNullableFormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { MessageModule } from 'primeng/message';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TranslateModule,
    ButtonModule,
    InputTextModule,
    PasswordModule,
    MessageModule,
  ],
  template: `
    <div class="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-gray-900 py-12 px-4 sm:px-6 lg:px-8">
      <div class="max-w-md w-full space-y-8">
        <div class="text-center">
          <h2 class="mt-6 text-3xl font-bold text-gray-900 dark:text-gray-100">
            {{ 'app.title' | translate }}
          </h2>
          <p class="mt-2 text-sm text-gray-600 dark:text-gray-400">
            {{ 'auth.loginRequired' | translate }}
          </p>
        </div>
        
        <form [formGroup]="loginForm" (ngSubmit)="onSubmit()" class="mt-8 space-y-6">
          <div class="space-y-4">
            <div>
              <label for="email" class="sr-only">{{ 'auth.email' | translate }}</label>
              <input
                id="email"
                type="email"
                formControlName="email"
                pInputText
                class="input-field"
                [placeholder]="'auth.email' | translate"
                required
                autocomplete="email"
              />
              @if (loginForm.get('email')?.invalid && loginForm.get('email')?.touched) {
                <p-message severity="error" text="Please enter a valid email address" class="mt-1" />
              }
            </div>
            
            <div>
              <label for="password" class="sr-only">{{ 'auth.password' | translate }}</label>
              <p-password
                id="password"
                formControlName="password"
                [placeholder]="'auth.password' | translate"
                class="w-full"
                inputStyleClass="input-field w-full"
                [toggleMask]="true"
                [feedback]="false"
                autocomplete="current-password"
                required
              />
              @if (loginForm.get('password')?.invalid && loginForm.get('password')?.touched) {
                <p-message severity="error" text="Password is required" class="mt-1" />
              }
            </div>
          </div>

          @if (errorMessage()) {
            <p-message severity="error" [text]="errorMessage()!" class="w-full" />
          }

          <div>
            <button
              type="submit"
              class="btn-primary w-full"
              [disabled]="loginForm.invalid || loading()"
            >
              @if (loading()) {
                <i class="pi pi-spinner pi-spin mr-2"></i>
              }
              {{ 'auth.login' | translate }}
            </button>
          </div>

          <div class="text-sm text-gray-600 dark:text-gray-400 mt-4">
            <p class="font-medium">Demo accounts:</p>
            <p>Customer: customer&#64;example.org / os7dy9dssdKHahdk</p>
            <p>Manager: manager&#64;example.org / iiysaiuuIYT&^d5sd</p>
          </div>
        </form>
      </div>
    </div>
  `,
})
export class LoginComponent {
  readonly #authService = inject(AuthService);
  readonly #router = inject(Router);
  readonly #fb = inject(NonNullableFormBuilder);

  protected readonly loading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  protected readonly loginForm = this.#fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  protected async onSubmit(): Promise<void> {
    if (this.loginForm.invalid) return;

    this.loading.set(true);
    this.errorMessage.set(null);

    try {
      const { email, password } = this.loginForm.getRawValue();
      const success = await this.#authService.login(email, password);

      if (success) {
        this.#router.navigate(['/dashboard']);
      } else {
        this.errorMessage.set('Invalid email or password');
      }
    } catch (error) {
      this.errorMessage.set('An unexpected error occurred');
    } finally {
      this.loading.set(false);
    }
  }
}