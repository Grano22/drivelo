import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, NonNullableFormBuilder, Validators } from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { InputNumberModule } from 'primeng/inputnumber';
import { DatePickerModule } from 'primeng/datepicker';
import { MessageModule } from 'primeng/message';
import { CardModule } from 'primeng/card';
import { AppStore } from '../../store/app.store';
import { User, UserStatus, UserRole, CreateUserSchema } from '../../types/user.types';

@Component({
  selector: 'app-add-client',
  standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        TranslateModule,
        ButtonModule,
        InputTextModule,
        AutoCompleteModule,
        InputNumberModule,
        DatePickerModule,
        MessageModule,
        CardModule,
        RouterLink,
    ],
  template: `
    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div class="mb-8">
        <button
          routerLink="/clients"
          class="btn-secondary mb-4"
        >
          <i class="pi pi-arrow-left mr-2"></i>
          {{ 'app.back' | translate }}
        </button>

        <h1 class="text-3xl font-bold text-gray-900 dark:text-gray-100">
          {{ 'clients.addClient' | translate }}
        </h1>
      </div>

      <div class="card p-8">
        <form [formGroup]="clientForm" (ngSubmit)="onSubmit()" class="space-y-8">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label for="firstName" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                {{ 'clients.firstName' | translate }} *
              </label>
              <input
                id="firstName"
                type="text"
                formControlName="firstName"
                pInputText
                class="input-field"
                [placeholder]="'clients.firstName' | translate"
                required
              />
              @if (clientForm.get('firstName')?.invalid && clientForm.get('firstName')?.touched) {
                <p-message severity="error" text="First name is required" class="mt-1" />
              }
            </div>

            <div>
              <label for="lastName" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                {{ 'clients.lastName' | translate }} *
              </label>
              <input
                id="lastName"
                type="text"
                formControlName="lastName"
                pInputText
                class="input-field"
                [placeholder]="'clients.lastName' | translate"
                required
              />
              @if (clientForm.get('lastName')?.invalid && clientForm.get('lastName')?.touched) {
                <p-message severity="error" text="Last name is required" class="mt-1" />
              }
            </div>

            <div>
              <label for="email" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                {{ 'clients.email' | translate }} *
              </label>
              <input
                id="email"
                type="email"
                formControlName="email"
                pInputText
                class="input-field"
                [placeholder]="'clients.email' | translate"
                required
                autocomplete="email"
              />
              @if (clientForm.get('email')?.invalid && clientForm.get('email')?.touched) {
                <p-message severity="error" text="Please enter a valid email address" class="mt-1" />
              }
            </div>

            <div>
              <label for="phone" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                {{ 'clients.phone' | translate }} *
              </label>
              <input
                id="phone"
                type="tel"
                formControlName="phone"
                pInputText
                class="input-field"
                [placeholder]="'clients.phone' | translate"
                required
                autocomplete="tel"
              />
              @if (clientForm.get('phone')?.invalid && clientForm.get('phone')?.touched) {
                <p-message severity="error" text="Please enter a valid phone number" class="mt-1" />
              }
            </div>

            <div>
              <label for="birthDate" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                {{ 'clients.birthDate' | translate }} *
              </label>
              <p-datepicker
                [(ngModel)]="yearRange"
                id="birthDate"
                formControlName="birthDate"
                dateFormat="yy-mm-dd"
                [maxDate]="maxBirthDate"
                styleClass="w-full"
                inputStyleClass="input-field"
              />
              @if (clientForm.get('birthDate')?.invalid && clientForm.get('birthDate')?.touched) {
                <p-message severity="error" text="Birth date is required" class="mt-1" />
              }
            </div>

            <div>
              <label for="credits" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                {{ 'clients.credits' | translate }} *
              </label>
              <p-inputNumber
                id="credits"
                formControlName="credits"
                mode="currency"
                currency="GBP"
                locale="en-GB"
                [min]="0"
                styleClass="w-full"
                inputStyleClass="input-field"
              />
            </div>
          </div>

          <div class="border-t border-gray-200 dark:border-gray-700 pt-6">
            <h3 class="text-lg font-semibold mb-4 text-gray-900 dark:text-gray-100">
              {{ 'clients.address' | translate }}
            </h3>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="md:col-span-2">
                <label for="street" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  {{ 'clients.street' | translate }} *
                </label>
                <input
                  id="street"
                  type="text"
                  formControlName="street"
                  pInputText
                  class="input-field"
                  [placeholder]="'clients.street' | translate"
                  required
                />
              </div>

              <div>
                <label for="zipCode" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  {{ 'clients.zipCode' | translate }} *
                </label>
                <input
                  id="zipCode"
                  type="text"
                  formControlName="zipCode"
                  pInputText
                  class="input-field"
                  placeholder="00-000"
                  pattern="[0-9]{2}-[0-9]{3}"
                  required
                />
              </div>

              <div>
                <label for="city" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  {{ 'clients.city' | translate }} *
                </label>
                <input
                  id="city"
                  type="text"
                  formControlName="city"
                  pInputText
                  class="input-field"
                  [placeholder]="'clients.city' | translate"
                  required
                />
              </div>

              <div>
                <label for="voivodeship" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  {{ 'clients.voivodeship' | translate }} *
                </label>
                <input
                  id="voivodeship"
                  type="text"
                  formControlName="voivodeship"
                  pInputText
                  class="input-field"
                  [placeholder]="'clients.voivodeship' | translate"
                  required
                />
              </div>

              <div>
                <label for="country" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  {{ 'clients.country' | translate }} *
                </label>
                <input
                  id="country"
                  type="text"
                  formControlName="country"
                  pInputText
                  class="input-field"
                  [placeholder]="'clients.country' | translate"
                  required
                />
              </div>
            </div>
          </div>

          @if (errorMessage()) {
              <p-message severity="error" class="w-full">{{ errorMessage() }}</p-message>
          }

          <div class="flex justify-end space-x-4 pt-6 border-t border-gray-200 dark:border-gray-700">
            <button
              type="button"
              routerLink="/clients"
              class="btn-secondary"
            >
              {{ 'app.cancel' | translate }}
            </button>
            <button
              type="submit"
              class="btn-primary"
              [disabled]="clientForm.invalid || loading()"
            >
              @if (loading()) {
                <i class="pi pi-spinner pi-spin mr-2"></i>
              }
              {{ 'app.save' | translate }}
            </button>
          </div>
        </form>
      </div>
    </div>
  `,
})
export class AddClientComponent {
  protected readonly store = inject(AppStore);
  readonly #router = inject(Router);
  readonly #fb = inject(NonNullableFormBuilder);

  protected readonly loading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  
  protected readonly maxBirthDate = new Date(Date.now() - 18 * 365 * 24 * 60 * 60 * 1000);
  protected yearRange = `${new Date().getFullYear() - 100}:${new Date().getFullYear() - 18}`;

  protected readonly statusOptions = Object.values(UserStatus).map(status => ({
    label: status.toUpperCase(),
    value: status,
  }));

  protected readonly clientForm = this.#fb.group({
    firstName: ['', [Validators.required, Validators.minLength(2)]],
    lastName: ['', [Validators.required, Validators.minLength(2)]],
    email: ['', [Validators.required, Validators.email]],
    phone: ['', [Validators.required, Validators.pattern(/^\+?\d{9,15}$/)]],
    birthDate: [null as Date | null, [Validators.required]],
    credits: [0, [Validators.required, Validators.min(0)]],
    street: ['', [Validators.required]],
    zipCode: ['', [Validators.required, Validators.pattern(/^\d{2}-\d{3}$/)]],
    city: ['', [Validators.required]],
    voivodeship: ['', [Validators.required]],
    country: ['United Kingdom', [Validators.required]],
  });

  // protected clearFilters(): void {
  //   this.filterForm.reset();
  // }

  protected async onSubmit(): Promise<void> {
    if (this.clientForm.invalid) {
      this.clientForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);

    try {
      const formValue = this.clientForm.getRawValue();
      
      const newUser: User = {
        id: crypto.randomUUID(),
        firstName: formValue.firstName,
        lastName: formValue.lastName,
        email: formValue.email,
        phone: formValue.phone,
        birthDate: formValue.birthDate!.toISOString().split('T')[0],
        address: {
          street: formValue.street,
          zipCode: formValue.zipCode,
          city: formValue.city,
          voivodeship: formValue.voivodeship,
          country: formValue.country,
        },
        credits: formValue.credits,
        status: UserStatus.ACTIVE,
        roles: [UserRole.CUSTOMER],
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      };

      const validatedUser = CreateUserSchema.parse({
        ...newUser,
        birthDate: newUser.birthDate,
      });

      await new Promise(resolve => setTimeout(resolve, 1000));

      this.store.addUser({ ...validatedUser, id: newUser.id, createdAt: newUser.createdAt, updatedAt: newUser.updatedAt });
      this.#router.navigate(['/clients']);
    } catch (error) {
      this.errorMessage.set('Failed to create client. Please check all fields.');
    } finally {
      this.loading.set(false);
    }
  }
}