import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, NonNullableFormBuilder, Validators } from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { TextareaModule } from 'primeng/textarea';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { InputNumberModule } from 'primeng/inputnumber';
import { MultiSelectModule } from 'primeng/multiselect';
import { MessageModule } from 'primeng/message';
import { CardModule } from 'primeng/card';
import { AppStore } from '../../store/app.store';
import { Car, VehicleType, GearboxType, EngineType, CarStatus, AmenityType, CreateCarSchema } from '../../types/car.types';

@Component({
  selector: 'app-add-car',
  standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        TranslateModule,
        ButtonModule,
        InputTextModule,
        TextareaModule,
        AutoCompleteModule,
        InputNumberModule,
        MultiSelectModule,
        MessageModule,
        CardModule,
        RouterLink,
    ],
  template: `
    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div class="mb-8">
        <button
          routerLink="/fleet-management"
          class="btn-secondary mb-4"
        >
          <i class="pi pi-arrow-left mr-2"></i>
          {{ 'app.back' | translate }}
        </button>

        <h1 class="text-3xl font-bold text-gray-900 dark:text-gray-100">
          {{ 'cars.addCar' | translate }}
        </h1>
      </div>

      <div class="card p-8">
        <form [formGroup]="carForm" (ngSubmit)="onSubmit()" class="space-y-8">
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
                (completeMethod)="filterVehicleTypes($event)"
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
                (completeMethod)="filterGearboxTypes($event)"
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
                (completeMethod)="filterEngineTypes($event)"
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
              <label for="fuelConsumption" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
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
            <p-message severity="error" [text]="errorMessage()!" class="w-full" />
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
      </div>
    </div>
  `,
})
export class AddCarComponent {
  protected readonly store = inject(AppStore);
  readonly #router = inject(Router);
  readonly #fb = inject(NonNullableFormBuilder);

  protected readonly loading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
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

  protected readonly filteredVehicleTypes = signal(this.vehicleTypeOptions);
  protected readonly filteredGearboxTypes = signal(this.gearboxTypeOptions);
  protected readonly filteredEngineTypes = signal(this.engineTypeOptions);

  protected filterVehicleTypes(event: { query?: string }): void {
    const q = (event.query || '').toLowerCase();
    this.filteredVehicleTypes.set(
      !q ? this.vehicleTypeOptions : this.vehicleTypeOptions.filter(o => o.label.toLowerCase().includes(q))
    );
  }
  protected filterGearboxTypes(event: { query?: string }): void {
    const q = (event.query || '').toLowerCase();
    this.filteredGearboxTypes.set(
      !q ? this.gearboxTypeOptions : this.gearboxTypeOptions.filter(o => o.label.toLowerCase().includes(q))
    );
  }
  protected filterEngineTypes(event: { query?: string }): void {
    const q = (event.query || '').toLowerCase();
    this.filteredEngineTypes.set(
      !q ? this.engineTypeOptions : this.engineTypeOptions.filter(o => o.label.toLowerCase().includes(q))
    );
  }

  protected readonly amenityOptions = Object.values(AmenityType).map(amenity => ({
    label: amenity.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase()),
    value: amenity,
  }));

  protected readonly carForm = this.#fb.group({
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

  protected async onSubmit(): Promise<void> {
    if (this.carForm.invalid) {
      this.carForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);

    try {
      const formValue = this.carForm.getRawValue();
      
      const newCar: Car = {
        id: crypto.randomUUID(),
        brand: formValue.brand,
        model: formValue.model,
        year: formValue.year,
        vehicleType: formValue.vehicleType!,
        maxCapacity: formValue.maxCapacity,
        pricePerDay: formValue.pricePerDay,
        status: CarStatus.AVAILABLE,
        gearboxType: formValue.gearboxType!,
        engineType: formValue.engineType!,
        amenities: formValue.amenities,
        description: formValue.description || undefined,
        imageUrl: formValue.imageUrl || undefined,
        mileage: formValue.mileage,
        fuelConsumption: formValue.fuelConsumption,
        maxRentalDays: formValue.maxRentalDays,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      };

      const validatedCar = CreateCarSchema.parse({
        ...newCar,
        id: undefined,
        createdAt: undefined,
        updatedAt: undefined,
      });

      await new Promise(resolve => setTimeout(resolve, 1000));

      this.store.addCar(newCar);
      this.#router.navigate(['/fleet-management']);
    } catch (error) {
      this.errorMessage.set('Failed to create car. Please check all fields.');
    } finally {
      this.loading.set(false);
    }
  }
}