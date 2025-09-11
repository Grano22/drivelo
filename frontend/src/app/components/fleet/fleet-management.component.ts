import { Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { ImageModule } from 'primeng/image';
import { AppStore } from '../../store/app.store';
import { CarRentalOffer, CarStatus } from '../../types/car.types';
import {CarFleetInternalHttpClients} from "../../services/car-fleet-internal-http-clients";

@Component({
  selector: 'app-fleet-management',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    TranslateModule,
    TableModule,
    ButtonModule,
    TagModule,
    ImageModule,
  ],
  template: `
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div class="flex justify-between items-center mb-8">
        <h1 class="text-3xl font-bold text-gray-900 dark:text-gray-100">
          Fleet Management
        </h1>
        <button
          routerLink="/fleet-management/add"
          class="btn-primary"
        >
          <i class="pi pi-plus mr-2"></i>
          {{ 'cars.addCar' | translate }}
        </button>
      </div>

      <div class="card">
        <p-table
          [value]="filteredCarOffers()"
          [loading]="store.loading()"
          [paginator]="true"
          [rows]="15"
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
              <th>{{ 'cars.pricePerDay' | translate }}</th>
              <th>{{ 'cars.status' | translate }}</th>
              <th>{{ 'cars.mileage' | translate }}</th>
              <th>{{ 'cars.maxRentalDays' | translate }}</th>
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
              <td class="font-semibold text-primary-600 dark:text-primary-400">
                £{{ car.pricePerDay }}
              </td>
              <td>
                <p-tag
                  [value]="car.status"
                  [severity]="getStatusSeverity(car.status)"
                />
              </td>
              <td>{{ formatNumber(car.mileage) }} km</td>
              <td>{{ car.maxRentalDays }} days</td>
              <td>
                <div class="flex gap-2">
                  <button
                    [routerLink]="['/fleet-management/edit', car.id]"
                    class="btn-secondary text-sm py-1 px-3"
                  >
                    {{ 'app.edit' | translate }}
                  </button>
                </div>
              </td>
            </tr>
          </ng-template>
          
          <ng-template pTemplate="emptymessage">
            <tr>
              <td colspan="10" class="text-center py-8 text-gray-500 dark:text-gray-400">
                No cars in fleet
              </td>
            </tr>
          </ng-template>
        </p-table>
      </div>
    </div>
  `,
})
export class FleetManagementComponent {
  protected readonly store = inject(AppStore);
  readonly #carFleetInternalHttpClients = inject(CarFleetInternalHttpClients);

    protected readonly filteredCarOffers = computed(() => {
        return this.store.cars();
    });

  constructor() {
      this.#initializeData();
  }

  protected getStatusSeverity(status: CarStatus): 'success' | 'info' | 'warning' | 'danger' {
    switch (status) {
      case CarStatus.AVAILABLE:
        return 'success';
      case CarStatus.RENTED:
        return 'info';
      case CarStatus.MAINTENANCE:
        return 'warning';
      case CarStatus.OUT_OF_SERVICE:
        return 'danger';
      default:
        return 'warning';
    }
  }

  protected formatNumber(num: number): string {
    return new Intl.NumberFormat('en-GB').format(num);
  }

  #initializeData() {
      this.#carFleetInternalHttpClients.getCarRentalOffers().subscribe({
          next: carRentalOffers => {
              this.store.setCars(carRentalOffers);
          },
          error: error => {
              console.error('Error fetching car rental offers:', error);
          }
      });
  }
}