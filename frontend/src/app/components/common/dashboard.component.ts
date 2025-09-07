import {Component, computed, inject} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { AppStore } from '../../store/app.store';
import { UserRole } from '../../types/user.types';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, TranslateModule, CardModule, ButtonModule],
  template: `
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900 dark:text-gray-100 mb-2">
          Welcome, {{ store.currentUser()?.firstName }}!
        </h1>
        <p class="text-gray-600 dark:text-gray-400">
          {{ 'navigation.dashboard' | translate }}
        </p>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        @if (store.isCustomer()) {
          <div class="card p-6 hover:shadow-xl transition-shadow duration-300">
            <div class="flex items-center justify-between mb-4">
              <div class="bg-primary-100 dark:bg-primary-900 p-3 rounded-lg">
                <i class="pi pi-car text-2xl text-primary-600 dark:text-primary-400"></i>
              </div>
              <div class="text-right">
                <p class="text-2xl font-bold text-gray-900 dark:text-gray-100">
                  {{ store.availableCars().length }}
                </p>
                <p class="text-sm text-gray-600 dark:text-gray-400">Available Cars</p>
              </div>
            </div>
            <button routerLink="/cars" class="btn-primary w-full">
              Browse Cars
            </button>
          </div>

          <div class="card p-6 hover:shadow-xl transition-shadow duration-300">
              <div class="flex items-center justify-between mb-4">
                  <div class="bg-primary-100 dark:bg-primary-900 p-3 rounded-lg">
                      <i class="pi pi-car text-2xl text-primary-600 dark:text-primary-400"></i>
                  </div>
                  <div class="text-right">
                      <p class="text-2xl font-bold text-gray-900 dark:text-gray-100">
                          0
                      </p>
                      <p class="text-sm text-gray-600 dark:text-gray-400">Rented cars</p>
                  </div>
              </div>
              <button routerLink="/rented-cars" class="btn-primary w-full">
                  Browse Rented Cars
              </button>
          </div>

          <div class="card p-6">
            <div class="flex items-center justify-between mb-4">
              <div class="bg-secondary-100 dark:bg-secondary-900 p-3 rounded-lg">
                <i class="pi pi-wallet text-2xl text-secondary-600 dark:text-secondary-400"></i>
              </div>
              <div class="text-right">
                <p class="text-2xl font-bold text-gray-900 dark:text-gray-100">
                  £{{ store.currentUser()?.credits || 0 }}
                </p>
                <p class="text-sm text-gray-600 dark:text-gray-400">Available Credits</p>
              </div>
            </div>
            <p class="text-sm text-gray-600 dark:text-gray-400">
              Your current credit balance for rentals
            </p>
          </div>
        }

        @if (store.isManager()) {
          <div class="card p-6 hover:shadow-xl transition-shadow duration-300">
            <div class="flex items-center justify-between mb-4">
              <div class="bg-primary-100 dark:bg-primary-900 p-3 rounded-lg">
                <i class="pi pi-users text-2xl text-primary-600 dark:text-primary-400"></i>
              </div>
              <div class="text-right">
                <p class="text-2xl font-bold text-gray-900 dark:text-gray-100">
                  {{ customersCount() }}
                </p>
                <p class="text-sm text-gray-600 dark:text-gray-400">Clients</p>
              </div>
            </div>
            <button routerLink="/clients" class="btn-primary w-full">
              Manage Clients
            </button>
          </div>

          <div class="card p-6 hover:shadow-xl transition-shadow duration-300">
            <div class="flex items-center justify-between mb-4">
              <div class="bg-secondary-100 dark:bg-secondary-900 p-3 rounded-lg">
                <i class="pi pi-car text-2xl text-secondary-600 dark:text-secondary-400"></i>
              </div>
              <div class="text-right">
                <p class="text-2xl font-bold text-gray-900 dark:text-gray-100">
                  {{ store.cars().length }}
                </p>
                <p class="text-sm text-gray-600 dark:text-gray-400">Total Cars</p>
              </div>
            </div>
            <button routerLink="/fleet-management" class="btn-primary w-full">
              Manage Fleet
            </button>
          </div>

          <div class="card p-6">
            <div class="flex items-center justify-between mb-4">
              <div class="bg-accent-100 dark:bg-accent-900 p-3 rounded-lg">
                <i class="pi pi-chart-line text-2xl text-accent-600 dark:text-accent-400"></i>
              </div>
              <div class="text-right">
                <p class="text-2xl font-bold text-gray-900 dark:text-gray-100">
                  {{ store.availableCars().length }}
                </p>
                <p class="text-sm text-gray-600 dark:text-gray-400">Available</p>
              </div>
            </div>
            <p class="text-sm text-gray-600 dark:text-gray-400">
              Cars ready for rental
            </p>
          </div>
        }
      </div>
    </div>
  `,
})
export class DashboardComponent {
    protected readonly store = inject(AppStore);
    readonly customersCount = computed(() =>
        this.store.customers().length
    );

}