import {Component, inject, input, signal} from '@angular/core';
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
import {UserStatus} from '../../types/user.types';
import {CustomerInternalHttpClient} from "../../services/customer-internal-http-client";
import {SaveClientDetailsForm, SaveClientDetailsFormGroup} from "./save-client-details-form";

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
        SaveClientDetailsForm,
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
                <app-save-client-details-form [loading]="loading()" [errorMessage]="errorMessage()" (onSubmit)="onSubmit($event)"/>
            </div>
        </div>
    `,
})
export class AddClientComponent {
    protected readonly store = inject(AppStore);
    readonly #customerInternalHttpClient = inject(CustomerInternalHttpClient);
    readonly #router = inject(Router);

    protected readonly loading = signal(false);
    protected readonly errorMessage = signal<string | null>(null);

    protected async onSubmit(clientForm: SaveClientDetailsFormGroup): Promise<void> {
        if (clientForm.invalid) {
            clientForm.markAllAsTouched();
            return;
        }

        this.loading.set(true);
        this.errorMessage.set(null);

        try {
            const formValue = clientForm.getRawValue();

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
                next: async () => {
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