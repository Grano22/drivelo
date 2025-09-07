import {Component, inject, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ReactiveFormsModule, NonNullableFormBuilder, Validators} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {TranslateModule} from '@ngx-translate/core';
import {ButtonModule} from 'primeng/button';
import {InputTextModule} from 'primeng/inputtext';
import {AutoCompleteModule} from 'primeng/autocomplete';
import {InputNumberModule} from 'primeng/inputnumber';
import {DatePickerModule} from 'primeng/datepicker';
import {MessageModule} from 'primeng/message';
import {CardModule} from 'primeng/card';
import {AppStore} from '../../store/app.store';
import {User, UserStatus, UserRole, CreateUserSchema} from '../../types/user.types';
import {CustomerInternalHttpClient} from "../../services/customer-internal-http-client";

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
                            <label for="firstName"
                                   class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
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
                                <p-message severity="error" text="First name is required" class="mt-1"/>
                            }
                        </div>

                        <div>
                            <label for="lastName"
                                   class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
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
                                <p-message severity="error" text="Last name is required" class="mt-1"/>
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
                                <p-message severity="error" text="Please enter a valid email address" class="mt-1"/>
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
                                <p-message severity="error" text="Please enter a valid phone number" class="mt-1"/>
                            }
                        </div>

                        <div>
                            <label for="birthDate"
                                   class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                                {{ 'clients.birthDate' | translate }} *
                            </label>
                            <p-datepicker
                                [(ngModel)]="yearRange"
                                id="birthDate"
                                formControlName="birthDate"
                                dateFormat="yy-mm-dd"
                                [maxDate]="maxBirthDate"
                                class="w-full"
                                inputclass="input-field"
                            />
                            @if (clientForm.get('birthDate')?.invalid && clientForm.get('birthDate')?.touched) {
                                <p-message severity="error" text="Birth date is required" class="mt-1"/>
                            }
                        </div>

                        <div>
                            <label for="credits"
                                   class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                                {{ 'clients.credits' | translate }} *
                            </label>
                            <p-inputNumber
                                    id="credits"
                                    formControlName="credits"
                                    mode="currency"
                                    currency="GBP"
                                    locale="en-GB"
                                    [min]="0"
                                    class="w-full"
                                    inputclass="input-field"
                            />
                        </div>

                        <div>
                            <label for="password"
                                   class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                                {{ 'clients.password' | translate }} *
                            </label>
                            <input
                                    id="password"
                                    type="password"
                                    formControlName="password"
                                    pInputText
                                    class="input-field"
                                    [placeholder]="'clients.password' | translate"
                                    required
                            />
                            @if (clientForm.get('password')?.invalid && clientForm.get('password')?.touched) {
                                <p-message severity="error" text="Password is required" class="mt-1"/>
                            }
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
    readonly #customerInternalHttpClient = inject(CustomerInternalHttpClient);
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
        password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(32)]],
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

            this.#customerInternalHttpClient.addCustomer({
                firstName: formValue.firstName,
                lastName: formValue.lastName,
                email: formValue.email,
                phone: formValue.phone,
                birthDate: formValue.birthDate!.toISOString().split('T')[0],
                status: UserStatus.ACTIVE,
                password: formValue.password,
                credits: formValue.credits,
            }).subscribe({
                next: async (response) => {
                    this.loading.set(false);
                    await this.#router.navigate(['/clients']);
                },
                error: (error) => {
                    this.loading.set(false);
                    throw Error('Error creating client:' + error.message);
                }
            });
        } catch (error) {
            this.errorMessage.set('Failed to create client. Please check all fields.');
        }
    }
}