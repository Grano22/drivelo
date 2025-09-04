import { Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, NonNullableFormBuilder } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { MultiSelectModule } from 'primeng/multiselect';
import { TagModule } from 'primeng/tag';
import { ImageModule } from 'primeng/image';
import { AppStore } from '../../store/app.store';
import { MockDataService } from '../../services/mock-data.service';
import { Car, VehicleType, GearboxType, EngineType, AmenityType } from '../../types/car.types';

@Component({
  selector: 'app-car-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    TranslateModule,
    TableModule,
    ButtonModule,
    InputTextModule,
    AutoCompleteModule,
    MultiSelectModule,
    TagModule,
    ImageModule,
  ],
  template: `
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900 dark:text-gray-100">
          {{ 'cars.title' | translate }}
        </h1>
      </div>

      <div class="card p-6 mb-6">
        <h2 class="text-lg font-semibold mb-4 text-gray-900 dark:text-gray-100">
          {{ 'cars.filters' | translate }}
        </h2>
        
        <form [formGroup]="filterForm" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <div>
            <label for="search" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
              {{ 'app.search' | translate }}
            </label>
            <input
              id="search"
              type="text"
              formControlName="search"
              pInputText
              class="input-field"
              [placeholder]="'app.search' | translate"
            />
          </div>

          <div>
            <label for="vehicleType" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
              {{ 'cars.type' | translate }}
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
            <label for="engineType" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
              {{ 'cars.engine' | translate }}
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
              [maxSelectedLabels]="2"
            />
          </div>
        </form>

        <div class="flex justify-end mt-4">
          <button
            type="button"
            (click)="clearFilters()"
            class="btn-secondary"
          >
            {{ 'cars.clearFilters' | translate }}
          </button>
        </div>
      </div>

      <div class="card">
        <p-table
          [value]="filteredCars()"
          [loading]="store.loading()"
          [paginator]="true"
          [rows]="10"
          [showCurrentPageReport]="true"
          currentPageReportTemplate="Showing {first} to {last} of {totalRecords} cars"
          class="p-datatable-sm"
        >
          <ng-template pTemplate="header">
            <tr>
              <th>Image</th>
              <th>{{ 'cars.brand' | translate }}</th>
              <th>{{ 'cars.model' | translate }}</th>
              <th>{{ 'cars.year' | translate }}</th>
              <th>{{ 'cars.type' | translate }}</th>
              <th>{{ 'cars.capacity' | translate }}</th>
              <th>{{ 'cars.pricePerDay' | translate }}</th>
              <th>{{ 'cars.gearbox' | translate }}</th>
              <th>{{ 'cars.engine' | translate }}</th>
              <th>{{ 'cars.amenities' | translate }}</th>
              <th>Actions</th>
            </tr>
          </ng-template>
          
          <ng-template pTemplate="body" let-car>
            <tr class="hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors duration-200">
              <td>
                <p-image
                  [src]="car.imageUrl || '/assets/images/car-placeholder.jpg'"
                  alt="Car image"
                  width="80"
                  height="60"
                  class="rounded-lg object-cover"
                />
              </td>
              <td class="font-medium">{{ car.brand }}</td>
              <td>{{ car.model }}</td>
              <td>{{ car.year }}</td>
              <td>
                <span class="capitalize">{{ ('vehicleTypes.' + car.vehicleType) | translate }}</span>
              </td>
              <td>
                <div class="flex items-center">
                  <i class="pi pi-users mr-1 text-gray-500"></i>
                  {{ car.maxCapacity }}
                </div>
              </td>
              <td class="font-semibold text-primary-600 dark:text-primary-400">
                £{{ car.pricePerDay }}
              </td>
              <td>{{ ('gearboxTypes.' + car.gearboxType) | translate }}</td>
              <td>{{ ('engineTypes.' + car.engineType) | translate }}</td>
              <td>
                <div class="flex flex-wrap gap-1">
                  @for (amenity of car.amenities.slice(0, 3); track amenity) {
                    <p-tag
                      [value]="getAmenityIcon(amenity)"
                      severity="info"
                      class="text-xs"
                    />
                  }
                  @if (car.amenities.length > 3) {
                    <p-tag
                      [value]="'+' + (car.amenities.length - 3)"
                      severity="secondary"
                      class="text-xs"
                    />
                  }
                </div>
              </td>
              <td>
                <div class="flex gap-2">
                  <button
                    [routerLink]="['/cars', car.id]"
                    class="btn-secondary text-sm py-1 px-3"
                  >
                    {{ 'cars.viewDetails' | translate }}
                  </button>
                  <button
                    [routerLink]="['/rental', car.id]"
                    class="btn-primary text-sm py-1 px-3"
                  >
                    {{ 'cars.rentNow' | translate }}
                  </button>
                </div>
              </td>
            </tr>
          </ng-template>
          
          <ng-template pTemplate="emptymessage">
            <tr>
              <td colspan="11" class="text-center py-8 text-gray-500 dark:text-gray-400">
                {{ 'cars.noResults' | translate }}
              </td>
            </tr>
          </ng-template>
        </p-table>
      </div>
    </div>
  `,
})
export class CarListComponent {
  protected readonly store = inject(AppStore);
  readonly #mockDataService = inject(MockDataService);
  readonly #fb = inject(NonNullableFormBuilder);

  protected readonly filterForm = this.#fb.group({
    search: [''],
    vehicleType: [null as VehicleType | null],
    engineType: [null as EngineType | null],
    amenities: [[] as AmenityType[]],
  });

  protected readonly vehicleTypeOptions = Object.values(VehicleType).map(type => ({
    label: type.toUpperCase(),
    value: type,
  }));

  protected readonly engineTypeOptions = Object.values(EngineType).map(type => ({
    label: type.toUpperCase(),
    value: type,
  }));

  protected readonly amenityOptions = Object.values(AmenityType).map(amenity => ({
    label: amenity.replace(/_/g, ' ').toUpperCase(),
    value: amenity,
  }));

  protected readonly filteredVehicleTypes = signal(this.vehicleTypeOptions);
  protected readonly filteredEngineTypes = signal(this.engineTypeOptions);
  protected filterVehicleTypes(event: { query?: string }): void {
    const q = (event.query || '').toLowerCase();
    this.filteredVehicleTypes.set(
      !q ? this.vehicleTypeOptions : this.vehicleTypeOptions.filter(o => o.label.toLowerCase().includes(q))
    );
  }
  protected filterEngineTypes(event: { query?: string }): void {
    const q = (event.query || '').toLowerCase();
    this.filteredEngineTypes.set(
      !q ? this.engineTypeOptions : this.engineTypeOptions.filter(o => o.label.toLowerCase().includes(q))
    );
  }

  protected readonly filteredCars = computed(() => {
    const cars = this.store.availableCars();
    const filters = this.filterForm.getRawValue();

    return cars.filter(car => {
      const matchesSearch = !filters.search || 
        car.brand.toLowerCase().includes(filters.search.toLowerCase()) ||
        car.model.toLowerCase().includes(filters.search.toLowerCase());

      const matchesVehicleType = !filters.vehicleType || 
        car.vehicleType === filters.vehicleType;

      const matchesEngineType = !filters.engineType || 
        car.engineType === filters.engineType;

      const matchesAmenities = !filters.amenities.length || 
        filters.amenities.every(amenity => car.amenities.includes(amenity));

      return matchesSearch && matchesVehicleType && matchesEngineType && matchesAmenities;
    });
  });

  constructor() {
    this.#initializeData();
  }

  protected clearFilters(): void {
    this.filterForm.reset();
  }

  protected getAmenityIcon(amenity: AmenityType): string {
    const icons: Record<AmenityType, string> = {
      [AmenityType.AIR_CONDITIONING]: '❄️',
      [AmenityType.AUDIO_SYSTEM]: '🎵',
      [AmenityType.BIG_TRUNK]: '📦',
      [AmenityType.ANDROID_AUTO]: '📱',
      [AmenityType.APPLE_CARPLAY]: '🍎',
      [AmenityType.HEATED_SEATS]: '🔥',
      [AmenityType.SUNROOF]: '☀️',
      [AmenityType.GPS]: '🗺️',
      [AmenityType.BLUETOOTH]: '📶',
      [AmenityType.USB_PORTS]: '🔌',
    };
    return icons[amenity];
  }

  #initializeData(): void {
    const cars = this.#mockDataService.getCars();
    this.store.setCars(cars);
  }
}