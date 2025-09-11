import {Component, effect, inject, input, output, signal} from "@angular/core";
import {FormGroup, FormsModule, NonNullableFormBuilder, ReactiveFormsModule, Validators} from "@angular/forms";
import {InputNumber} from "primeng/inputnumber";
import {InputText} from "primeng/inputtext";
import {Message} from "primeng/message";
import {RouterLink} from "@angular/router";
import {TranslatePipe} from "@ngx-translate/core";
import {AutoComplete} from "primeng/autocomplete";
import {MultiSelect} from "primeng/multiselect";
import {PrimeTemplate} from "primeng/api";
import {Textarea} from "primeng/textarea";
import {AmenityType, EngineType, GearboxType, VehicleType} from "../../types/car.types";
import {FormControlsOf} from "../../types/utility.types";
import {filterAutocomplete} from "../../form/form.utils";

export type SaveCarDetails = {
    brand: string,
    model: string,
    year: number,
    vehicleType: VehicleType|null,
    maxCapacity: number,
    pricePerDay: number,
    gearboxType: GearboxType|null,
    engineType: EngineType|null,
    amenities: AmenityType[],
    description: string,
    imageUrl: string,
    mileage: number,
    fuelConsumption: number,
    maxRentalDays: number
};
export type SaveCarDetailsFormGroup = FormGroup<FormControlsOf<SaveCarDetails>>;

@Component({
    selector: 'app-save-car-form',
    imports: [
        FormsModule,
        InputNumber,
        InputText,
        Message,
        ReactiveFormsModule,
        RouterLink,
        TranslatePipe,
        AutoComplete,
        MultiSelect,
        PrimeTemplate,
        Textarea
    ],
    standalone: true,
    template: `
        <form [formGroup]="carForm" (ngSubmit)="onInternalSubmit()" class="space-y-8">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                    <label for="brand" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                        {{ 'cars.brand' | translate }} *
                    </label>
                    <input
                            id="brand"
                            type="text"
                            formControlName="brand"
                            pInputText
                            class="input-field"
                            [placeholder]="'cars.brand' | translate"
                            required
                    />
                </div>

                <div>
                    <label for="model" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                        {{ 'cars.model' | translate }} *
                    </label>
                    <input
                            id="model"
                            type="text"
                            formControlName="model"
                            pInputText
                            class="input-field"
                            [placeholder]="'cars.model' | translate"
                            required
                    />
                </div>

                <div>
                    <label for="year" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                        {{ 'cars.year' | translate }} *
                    </label>
                    <p-inputNumber
                            id="year"
                            formControlName="year"
                            [min]="1900"
                            [max]="currentYear + 1"
                            [useGrouping]="false"
                            class="w-full"
                            inputclass="input-field"
                    />
                </div>

                <div>
                    <label for="vehicleType" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                        {{ 'cars.type' | translate }} *
                    </label>
                    <p-autoComplete
                            id="vehicleType"
                            formControlName="vehicleType"
                            [suggestions]="filteredVehicleTypes()"
                            (completeMethod)="filterAutocomplete($event, filteredVehicleTypes, vehicleTypeOptions)"
                            [dropdown]="true"
                            optionLabel="label"
                            optionValue="value"
                            [placeholder]="'cars.type' | translate"
                            class="w-full"
                    >
                        <ng-template pTemplate="item" let-item>
                            <div class="flex items-center gap-2">
                                <span>{{ item.label }}</span>
                            </div>
                        </ng-template>
                        <ng-template pTemplate="selectedItem" let-item>
                            <span>{{ item?.label }}</span>
                        </ng-template>
                    </p-autoComplete>
                </div>

                <div>
                    <label for="maxCapacity" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                        {{ 'cars.capacity' | translate }} *
                    </label>
                    <p-inputNumber
                            id="maxCapacity"
                            formControlName="maxCapacity"
                            [min]="1"
                            [max]="20"
                            class="w-full"
                            inputclass="input-field"
                    />
                </div>

                <div>
                    <label for="pricePerDay" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                        {{ 'cars.pricePerDay' | translate }} *
                    </label>
                    <p-inputNumber
                            id="pricePerDay"
                            formControlName="pricePerDay"
                            mode="currency"
                            currency="GBP"
                            locale="en-GB"
                            [min]="0"
                            class="w-full"
                            inputclass="input-field"
                    />
                </div>

                <div>
                    <label for="gearboxType" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                        {{ 'cars.gearbox' | translate }} *
                    </label>
                    <p-autoComplete
                            id="gearboxType"
                            formControlName="gearboxType"
                            [suggestions]="filteredGearboxTypes()"
                            (completeMethod)="filterAutocomplete($event, filteredGearboxTypes, gearboxTypeOptions)"
                            [dropdown]="true"
                            optionLabel="label"
                            optionValue="value"
                            [placeholder]="'cars.gearbox' | translate"
                            class="w-full"
                    >
                        <ng-template pTemplate="item" let-item>
                            <div class="flex items-center gap-2">
                                <span>{{ item.label }}</span>
                            </div>
                        </ng-template>
                        <ng-template pTemplate="selectedItem" let-item>
                            <span>{{ item?.label }}</span>
                        </ng-template>
                    </p-autoComplete>
                </div>

                <div>
                    <label for="engineType" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                        {{ 'cars.engine' | translate }} *
                    </label>
                    <p-autoComplete
                            id="engineType"
                            formControlName="engineType"
                            [suggestions]="filteredEngineTypes()"
                            (completeMethod)="filterAutocomplete($event, filteredEngineTypes, engineTypeOptions)"
                            [dropdown]="true"
                            optionLabel="label"
                            optionValue="value"
                            [placeholder]="'cars.engine' | translate"
                            class="w-full"
                    >
                        <ng-template pTemplate="item" let-item>
                            <div class="flex items-center gap-2">
                                <span>{{ item.label }}</span>
                            </div>
                        </ng-template>
                        <ng-template pTemplate="selectedItem" let-item>
                            <span>{{ item?.label }}</span>
                        </ng-template>
                    </p-autoComplete>
                </div>

                <div>
                    <label for="mileage" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                        {{ 'cars.mileage' | translate }} (km) *
                    </label>
                    <p-inputNumber
                            id="mileage"
                            formControlName="mileage"
                            [min]="0"
                            class="w-full"
                            inputclass="input-field"
                    />
                </div>

                <div>
                    <label for="fuelConsumption"
                           class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                        {{ 'cars.fuelConsumption' | translate }} (L/100km)
                    </label>
                    <p-inputNumber
                            id="fuelConsumption"
                            formControlName="fuelConsumption"
                            [min]="0"
                            [minFractionDigits]="1"
                            [maxFractionDigits]="1"
                            class="w-full"
                            inputclass="input-field"
                    />
                </div>

                <div>
                    <label for="maxRentalDays" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                        {{ 'cars.maxRentalDays' | translate }} *
                    </label>
                    <p-inputNumber
                            id="maxRentalDays"
                            formControlName="maxRentalDays"
                            [min]="1"
                            [max]="365"
                            class="w-full"
                            inputclass="input-field"
                    />
                </div>

                <div>
                    <label for="imageUrl" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                        Image URL
                    </label>
                    <input
                            id="imageUrl"
                            type="url"
                            formControlName="imageUrl"
                            pInputText
                            class="input-field"
                            placeholder="https://example.com/car-image.jpg"
                    />
                </div>
            </div>

            <div>
                <label for="amenities" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                    {{ 'cars.amenities' | translate }}
                </label>
                <p-multiSelect
                        id="amenities"
                        formControlName="amenities"
                        [options]="amenityOptions"
                        optionLabel="label"
                        optionValue="value"
                        [placeholder]="'cars.amenities' | translate"
                        class="w-full"
                />
            </div>

            <div>
                <label for="description" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                    {{ 'cars.description' | translate }}
                </label>
                <textarea
                        id="description"
                        formControlName="description"
                        pTextarea
                        rows="4"
                        class="input-field resize-none"
                        [placeholder]="'cars.description' | translate"
                ></textarea>
            </div>

            @if (errorMessage()) {
                <p-message severity="error" class="w-full">{{ errorMessage()! }}</p-message>
            }

            <div class="flex justify-end space-x-4 pt-6 border-t border-gray-200 dark:border-gray-700">
                <button
                        type="button"
                        routerLink="/fleet-management"
                        class="btn-secondary"
                >
                    {{ 'app.cancel' | translate }}
                </button>
                <button
                        type="submit"
                        class="btn-primary"
                        [disabled]="carForm.invalid || loading()"
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
export class SaveCarForm {
    readonly #fb = inject(NonNullableFormBuilder);

    readonly loading = input(false);
    readonly errorMessage = input<string | null>(null);
    public readonly initialValue = input<Partial<SaveCarDetails>>();
    public readonly validationGroup = input<'add' | 'edit'>('add');

    protected readonly currentYear = new Date().getFullYear();

    protected readonly vehicleTypeOptions = Object.values(VehicleType).map(type => ({
        label: type.toUpperCase().replace('_', ' '),
        value: type,
    }));

    protected readonly gearboxTypeOptions = Object.values(GearboxType).map(type => ({
        label: type.toUpperCase().replace('_', ' '),
        value: type,
    }));

    protected readonly engineTypeOptions = Object.values(EngineType).map(type => ({
        label: type.toUpperCase(),
        value: type,
    }));

    protected readonly amenityOptions = Object.values(AmenityType).map(amenity => ({
        label: amenity.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase()),
        value: amenity,
    }));

    protected readonly filteredVehicleTypes = signal(this.vehicleTypeOptions);
    protected readonly filteredGearboxTypes = signal(this.gearboxTypeOptions);
    protected readonly filteredEngineTypes = signal(this.engineTypeOptions);

    protected carForm = this.#fb.group({
        brand: ['', [Validators.required, Validators.minLength(2)]],
        model: ['', [Validators.required, Validators.minLength(1)]],
        year: [this.currentYear, [Validators.required, Validators.min(1900), Validators.max(this.currentYear + 1)]],
        vehicleType: [null as VehicleType | null, [Validators.required]],
        maxCapacity: [5, [Validators.required, Validators.min(1), Validators.max(20)]],
        pricePerDay: [50, [Validators.required, Validators.min(0)]],
        gearboxType: [null as GearboxType | null, [Validators.required]],
        engineType: [null as EngineType | null, [Validators.required]],
        amenities: [[] as AmenityType[]],
        description: [''],
        imageUrl: [''],
        mileage: [0, [Validators.required, Validators.min(0)]],
        fuelConsumption: [0, [Validators.min(0)]],
        maxRentalDays: [14, [Validators.required, Validators.min(1), Validators.max(365)]],
    });

    public readonly onSubmit = output<typeof this.carForm>();

    constructor() {
        const effectRef = effect(
            () => {
                const initialValues = this.initialValue();

                if (initialValues) {
                    const validationSetPerGroup = [];

                    if (this.validationGroup() === 'add') {
                        validationSetPerGroup.push(Validators.required);
                    }

                    this.carForm = this.#fb.group({
                        brand: [initialValues.brand || '', [Validators.required, Validators.minLength(2)]],
                        model: [initialValues.model || '', [Validators.required, Validators.minLength(1)]],
                        year: [initialValues.year || this.currentYear, [Validators.required, Validators.min(1900), Validators.max(this.currentYear + 1)]],
                        vehicleType: [initialValues.vehicleType || null as VehicleType | null, [Validators.required]],
                        maxCapacity: [initialValues.maxCapacity || 5, [Validators.required, Validators.min(1), Validators.max(20)]],
                        pricePerDay: [initialValues.pricePerDay || 50, [Validators.required, Validators.min(0)]],
                        gearboxType: [initialValues.gearboxType || null as GearboxType | null, [Validators.required]],
                        engineType: [initialValues.engineType || null as EngineType | null, [Validators.required]],
                        amenities: [initialValues.amenities || [] as AmenityType[]],
                        description: [initialValues.description || ''],
                        imageUrl: [initialValues.imageUrl || ''],
                        mileage: [initialValues.mileage || 0, [Validators.required, Validators.min(0)]],
                        fuelConsumption: [initialValues.fuelConsumption || 0, [Validators.min(0)]],
                        maxRentalDays: [initialValues.maxRentalDays || 14, [Validators.required, Validators.min(1), Validators.max(365)]],
                    });
                }
                effectRef.destroy();
            },
            { manualCleanup: true }
        );
    }

    protected async onInternalSubmit(): Promise<void> {
        this.onSubmit.emit(this.carForm);
    }

    protected readonly filterAutocomplete = filterAutocomplete;
}