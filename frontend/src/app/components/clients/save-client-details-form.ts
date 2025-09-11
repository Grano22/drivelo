import {Component, effect, inject, input, output} from "@angular/core";
import {DatePicker} from "primeng/datepicker";
import {FormGroup, FormsModule, NonNullableFormBuilder, ReactiveFormsModule, Validators} from "@angular/forms";
import {InputNumber} from "primeng/inputnumber";
import {InputText} from "primeng/inputtext";
import {Message} from "primeng/message";
import {RouterLink} from "@angular/router";
import {TranslatePipe} from "@ngx-translate/core";
import {FormControlsOf} from "../../types/utility.types";
import {JsonPipe} from "@angular/common";

export type SaveClientDetails = {
    firstName: string,
    lastName: string
    email: string,
    phone: string,
    birthDate: Date | null,
    credits: number,
    password: string
};
export type SaveClientDetailsFormGroup = FormGroup<FormControlsOf<SaveClientDetails>>;

@Component({
    selector: 'app-save-client-details-form',
    imports: [
        DatePicker,
        FormsModule,
        InputNumber,
        InputText,
        Message,
        ReactiveFormsModule,
        RouterLink,
        TranslatePipe,
        JsonPipe
    ],
    standalone: true,
    template: `
        <form [formGroup]="clientForm" (ngSubmit)="onInternalSubmit()" class="space-y-8">
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
                        <p-message severity="error" class="mt-1">Please enter a valid phone number</p-message>
                    }
                </div>

                <div>
                    <label for="birthDate"
                           class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                        {{ 'clients.birthDate' | translate }} *
                    </label>
                    <p-datepicker
                        id="birthDate"
                        formControlName="birthDate"
                        dateFormat="yy-mm-dd"
                        [maxDate]="maxBirthDate"
                        class="w-full"
                        inputclass="input-field"
                        [defaultDate]="minBirthDate"
                        [showButtonBar]="true"
                        [showIcon]="true"
                        [placeholder]="'placeholder.datePicker' | translate"
                    />
                    @if (clientForm.get('birthDate')?.invalid && clientForm.get('birthDate')?.touched) {
                        <p-message severity="error" class="mt-1">Birth date is required</p-message>
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
                        [attr.required]="validationGroup() === 'add' ? 'required' : null"
                    />
                    @if (clientForm.get('password')?.invalid && clientForm.get('password')?.touched) {
                        <p-message severity="error" class="mt-1">Password is required</p-message>
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
    `
})
export class SaveClientDetailsForm {
    readonly #fb = inject(NonNullableFormBuilder);

    readonly minBirthYear = new Date().getFullYear() - 18 || 2007;
    readonly minBirthDate = new Date(this.minBirthYear, 0, 1);

    protected readonly maxBirthDate = new Date(Date.now() - 18 * 365 * 24 * 60 * 60 * 1000);

    public readonly loading = input(false);
    public readonly errorMessage = input<string | null>(null);
    public readonly initialValue = input<Partial<SaveClientDetails>>();
    public readonly validationGroup = input<'add' | 'edit'>('add');

    protected clientForm: SaveClientDetailsFormGroup = this.#fb.group({
        firstName: ['', [Validators.required, Validators.minLength(2)]],
        lastName: ['', [Validators.required, Validators.minLength(2)]],
        email: ['', [Validators.required, Validators.email]],
        phone: ['', [Validators.required, Validators.pattern(/^\+?\d{9,15}$/)]],
        birthDate: [null as Date | null, [Validators.required]],
        credits: [0, [Validators.required, Validators.min(0)]],
        password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(32)]],
    });

    public readonly onSubmit = output<typeof this.clientForm>();

    constructor() {
        const effectRef = effect(
            () => {
                const initialValues = this.initialValue();

                if (initialValues) {
                    const validationSetPerGroup = [];

                    if (this.validationGroup() === 'add') {
                        validationSetPerGroup.push(Validators.required);
                    }

                    this.clientForm = this.#fb.group({
                        firstName: [initialValues?.firstName || '', [...validationSetPerGroup, Validators.minLength(2)]],
                        lastName: [initialValues?.lastName || '', [...validationSetPerGroup, Validators.minLength(2)]],
                        email: [initialValues?.email || '', [...validationSetPerGroup, Validators.email]],
                        phone: [initialValues?.phone || '', [...validationSetPerGroup, Validators.pattern(/^\+?\d{9,15}$/)]],
                        birthDate: [(initialValues?.birthDate || null) as Date | null, [Validators.required]],
                        credits: [initialValues?.credits || 0, [...validationSetPerGroup, Validators.min(0)]],
                        password: ['', [...validationSetPerGroup, Validators.minLength(8), Validators.maxLength(32)]],
                    });
                }

                effectRef.destroy();
            },
            { manualCleanup: true }
        );
    }

    protected async onInternalSubmit(): Promise<void> {
        this.onSubmit.emit(this.clientForm);
    }
}