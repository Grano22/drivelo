import {Component, inject, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {NonNullableFormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {TranslateModule} from '@ngx-translate/core';
import {ButtonModule} from 'primeng/button';
import {InputTextModule} from 'primeng/inputtext';
import {TextareaModule} from 'primeng/textarea';
import {AutoCompleteModule} from 'primeng/autocomplete';
import {InputNumberModule} from 'primeng/inputnumber';
import {MultiSelectModule} from 'primeng/multiselect';
import {MessageModule} from 'primeng/message';
import {CardModule} from 'primeng/card';
import {AppStore} from '../../store/app.store';
import {AmenityType, CarStatus, EngineType, GearboxType, VehicleType} from '../../types/car.types';
import {CarFleetInternalHttpClients} from "../../services/car-fleet-internal-http-clients";
import {CarRentalOfferStatus} from "../../types/car-rental-offer.types";
import {SaveCarDetailsFormGroup, SaveCarForm} from "./save-car-form";

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
        SaveCarForm,
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
        <app-save-car-form [loading]="loading()" [errorMessage]="errorMessage()" (onSubmit)="onSubmit($event)"/>
      </div>
    </div>
  `,
})
export class AddCarComponent {
  protected readonly store = inject(AppStore);
  readonly #carFleetInternalHttpClients = inject(CarFleetInternalHttpClients);
  readonly #router = inject(Router);

  protected readonly loading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  protected async onSubmit(carForm: SaveCarDetailsFormGroup): Promise<void> {
    if (carForm.invalid) {
      carForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);

    try {
      const formValue = carForm.getRawValue();

      this.#carFleetInternalHttpClients.addCarWithRentalOffer({
          car: {
              name: `${formValue.brand} ${formValue.model}`,
              brand: formValue.brand,
              model: formValue.model,
              year: formValue.year,
              type: formValue.vehicleType!,
              maxCapacity: formValue.maxCapacity,
              status: CarStatus.AVAILABLE,
              gearboxType: formValue.gearboxType!,
              engineType: formValue.engineType!,
              amenities: formValue.amenities,
              description: /*formValue.description || */'',
              imageUrl: formValue.imageUrl || undefined,
              mileage: formValue.mileage,
              fuelConsumption: formValue.fuelConsumption
          },
          rentalOfferDetails: {
              pricePerDay: formValue.pricePerDay,
              status: CarRentalOfferStatus.ACTIVE,
              maxRentalDays: formValue.maxRentalDays,
              description: formValue.description
          }
      }).subscribe({
          next: () => {
              this.errorMessage.set(null);
              this.#router.navigate(['/fleet-management']);
          },
          error: (error) => {
              this.errorMessage.set(error.error.message);
          }
      });
    } catch (error) {
      this.errorMessage.set('Failed to create car. Please check all fields.');
    } finally {
      this.loading.set(false);
    }
  }
}