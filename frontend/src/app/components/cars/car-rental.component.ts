import { Component, computed, inject, signal, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, NonNullableFormBuilder, Validators } from '@angular/forms';
import {Router, ActivatedRoute, RouterModule} from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { ButtonModule } from 'primeng/button';
import { DatePickerModule } from 'primeng/datepicker';
import { InputNumberModule } from 'primeng/inputnumber';
import { MessageModule } from 'primeng/message';
import { CardModule } from 'primeng/card';
import { ImageModule } from 'primeng/image';
import { TagModule } from 'primeng/tag';
import { AppStore } from '../../store/app.store';
import { MockDataService } from '../../services/mock-data.service';
import {Car, AmenityType, CarStatus} from '../../types/car.types';

@Component({
  selector: 'app-car-rental',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TranslateModule,
    RouterModule,
    ButtonModule,
    DatePickerModule,
    InputNumberModule,
    MessageModule,
    CardModule,
    ImageModule,
    TagModule,
  ],
  template: `
    <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      @if (selectedCar(); as car) {
        <div class="mb-8">
          <button
            routerLink="/cars"
            class="btn-secondary mb-4"
          >
            <i class="pi pi-arrow-left mr-2"></i>
            {{ 'app.back' | translate }}
          </button>

          <h1 class="text-3xl font-bold text-gray-900 dark:text-gray-100">
            {{ 'rental.title' | translate }}: {{ car.brand }} {{ car.model }}
          </h1>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <div class="card p-6">
            <div class="mb-6">
              <p-image
                [src]="car.imageUrl || '/assets/images/car-placeholder.jpg'"
                [alt]="car.brand + ' ' + car.model"
                width="100%"
                height="300"
                class="rounded-lg object-cover w-full"
              />
            </div>

            <div class="space-y-4">
              <div class="grid grid-cols-2 gap-4">
                <div>
                  <span class="text-sm font-medium text-gray-600 dark:text-gray-400">
                    {{ 'cars.year' | translate }}
                  </span>
                  <p class="text-lg font-semibold">{{ car.year }}</p>
                </div>
                <div>
                  <span class="text-sm font-medium text-gray-600 dark:text-gray-400">
                    {{ 'cars.capacity' | translate }}
                  </span>
                  <p class="text-lg font-semibold">{{ car.maxCapacity }} persons</p>
                </div>
                <div>
                  <span class="text-sm font-medium text-gray-600 dark:text-gray-400">
                    {{ 'cars.gearbox' | translate }}
                  </span>
                  <p class="text-lg font-semibold">{{ ('gearboxTypes.' + car.gearboxType) | translate }}</p>
                </div>
                <div>
                  <span class="text-sm font-medium text-gray-600 dark:text-gray-400">
                    {{ 'cars.engine' | translate }}
                  </span>
                  <p class="text-lg font-semibold">{{ ('engineTypes.' + car.engineType) | translate }}</p>
                </div>
              </div>

              <div>
                <span class="text-sm font-medium text-gray-600 dark:text-gray-400 block mb-2">
                  {{ 'cars.amenities' | translate }}
                </span>
                <div class="flex flex-wrap gap-2">
                  @for (amenity of car.amenities; track amenity) {
                    <p-tag
                      [value]="getAmenityName(amenity)"
                      severity="info"
                      class="text-sm"
                    />
                  }
                </div>
              </div>

              @if (car.description) {
                <div>
                  <span class="text-sm font-medium text-gray-600 dark:text-gray-400 block mb-2">
                    {{ 'cars.description' | translate }}
                  </span>
                  <p class="text-gray-700 dark:text-gray-300">{{ car.description }}</p>
                </div>
              }
            </div>
          </div>

          <div class="card p-6">
            <h2 class="text-xl font-bold mb-6 text-gray-900 dark:text-gray-100">
              Rental Details
            </h2>

            <form [formGroup]="rentalForm" (ngSubmit)="onSubmit()">
              <div class="space-y-6">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label for="startDate" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                      {{ 'rental.startDate' | translate }}
                    </label>
                    <p-datepicker
                      id="startDate"
                      formControlName="startDate"
                      [minDate]="minDate"
                      [maxDate]="maxStartDate"
                      dateFormat="yy-mm-dd"
                      class="w-full"
                      inputclass="input-field"
                    />
                  </div>

                  <div>
                    <label for="endDate" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                      {{ 'rental.endDate' | translate }}
                    </label>
                    <p-datepicker
                      id="endDate"
                      formControlName="endDate"
                      [minDate]="minEndDate()"
                      [maxDate]="maxEndDate()"
                      dateFormat="yy-mm-dd"
                      class="w-full"
                      inputclass="input-field"
                    />
                  </div>
                </div>

                <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
                  <div class="flex justify-between items-center mb-2">
                    <span class="text-sm font-medium text-gray-600 dark:text-gray-400">
                      {{ 'rental.totalDays' | translate }}:
                    </span>
                    <span class="text-lg font-bold text-gray-900 dark:text-gray-100">
                      {{ totalDays() }}
                    </span>
                  </div>
                  <div class="flex justify-between items-center mb-2">
                    <span class="text-sm font-medium text-gray-600 dark:text-gray-400">
                      Daily Rate:
                    </span>
                    <span class="text-lg font-bold text-gray-900 dark:text-gray-100">
                      £{{ car.pricePerDay }}
                    </span>
                  </div>
                  <hr class="my-3 border-gray-300 dark:border-gray-600">
                  <div class="flex justify-between items-center">
                    <span class="text-lg font-bold text-gray-900 dark:text-gray-100">
                      {{ 'rental.totalPrice' | translate }}:
                    </span>
                    <span class="text-2xl font-bold text-primary-600 dark:text-primary-400">
                      £{{ totalPrice() }}
                    </span>
                  </div>
                </div>

                @if (errorMessage()) {
                  <p-message severity="error" [text]="errorMessage()!" class="w-full" />
                }

                <button
                  type="submit"
                  class="btn-primary w-full text-lg py-3"
                  [disabled]="rentalForm.invalid || loading() || totalPrice() > (store.currentUser()?.credits || 0)"
                >
                  @if (loading()) {
                    <i class="pi pi-spinner pi-spin mr-2"></i>
                  }
                  {{ 'rental.confirmRental' | translate }} (£{{ totalPrice() }})
                </button>

                @if (totalPrice() > (store.currentUser()?.credits || 0)) {
                  <p-message 
                    severity="warn" 
                    text="Insufficient credits. Current balance: £{{ store.currentUser()?.credits || 0 }}"
                    class="w-full" 
                  />
                }
              </div>
            </form>
          </div>
        </div>
      } @else {
        <div class="text-center py-8">
          <p class="text-gray-500 dark:text-gray-400">Car not found</p>
          <button routerLink="/cars" class="btn-primary mt-4">
            {{ 'app.back' | translate }}
          </button>
        </div>
      }
    </div>
  `,
})
export class CarRentalComponent {
  protected readonly store = inject(AppStore);
  readonly #mockDataService = inject(MockDataService);
  readonly #router = inject(Router);
  readonly #route = inject(ActivatedRoute);
  readonly #fb = inject(NonNullableFormBuilder);

  protected readonly loading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  
  protected readonly carId = signal<string | null>(null);
  protected readonly selectedCar = computed(() => {
    const id = this.carId();
    return id ? this.#mockDataService.getCarById(id) : null;
  });

  protected readonly minDate = new Date();
  protected readonly maxStartDate = new Date(Date.now() + 365 * 24 * 60 * 60 * 1000);

  protected readonly rentalForm = this.#fb.group({
    startDate: [new Date(), [Validators.required]],
    endDate: [new Date(Date.now() + 24 * 60 * 60 * 1000), [Validators.required]],
  });

  protected readonly minEndDate = computed(() => {
    const startDate = this.rentalForm.get('startDate')?.value;
    return startDate ? new Date(startDate.getTime() + 24 * 60 * 60 * 1000) : this.minDate;
  });

  protected readonly maxEndDate = computed(() => {
    const car = this.selectedCar();
    const startDate = this.rentalForm.get('startDate')?.value;
    
    if (!car || !startDate) return this.maxStartDate;
    
    const maxDate = new Date(startDate.getTime() + car.maxRentalDays * 24 * 60 * 60 * 1000);
    return maxDate;
  });

  protected readonly totalDays = computed(() => {
    const startDate = this.rentalForm.get('startDate')?.value;
    const endDate = this.rentalForm.get('endDate')?.value;
    
    if (!startDate || !endDate) return 0;
    
    const diffTime = Math.abs(endDate.getTime() - startDate.getTime());
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  });

  protected readonly totalPrice = computed(() => {
    const car = this.selectedCar();
    const days = this.totalDays();
    return car ? car.pricePerDay * days : 0;
  });

  constructor() {
    this.#route.params.subscribe(params => {
      this.carId.set(params['id']);
    });

    this.rentalForm.get('startDate')?.valueChanges.subscribe(() => {
      const endDateControl = this.rentalForm.get('endDate');
      const startDate = this.rentalForm.get('startDate')?.value;
      
      if (startDate && endDateControl?.value && endDateControl.value <= startDate) {
        endDateControl.setValue(new Date(startDate.getTime() + 24 * 60 * 60 * 1000));
      }
    });
  }

  protected async onSubmit(): Promise<void> {
    if (this.rentalForm.invalid || !this.selectedCar()) return;

    this.loading.set(true);
    this.errorMessage.set(null);

    try {
      const currentUser = this.store.currentUser();
      const totalCost = this.totalPrice();

      if (!currentUser || currentUser.credits < totalCost) {
        this.errorMessage.set('Insufficient credits for this rental');
        return;
      }

      await new Promise(resolve => setTimeout(resolve, 1500));

      this.store.updateCar(this.selectedCar()!.id, { status: CarStatus.RENTED });
      
      this.#router.navigate(['/cars'], {
        queryParams: { success: 'rental.rentalSuccess' }
      });
    } catch (error) {
      this.errorMessage.set('An unexpected error occurred');
    } finally {
      this.loading.set(false);
    }
  }

  protected getAmenityName(amenity: AmenityType): string {
    return amenity.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase());
  }
}