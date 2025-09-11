import {Component, computed, inject, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {NonNullableFormBuilder, ReactiveFormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {TranslateModule} from '@ngx-translate/core';
import {TableModule} from 'primeng/table';
import {ButtonModule} from 'primeng/button';
import {InputTextModule} from 'primeng/inputtext';
import {AutoCompleteModule} from 'primeng/autocomplete';
import {TagModule} from 'primeng/tag';
import {AppStore} from '../../store/app.store';
import {UserStatus} from '../../types/user.types';
import {CustomerInternalHttpClient} from "../../services/customer-internal-http-client";

@Component({
  selector: 'app-client-list',
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
    TagModule,
  ],
  template: `
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div class="flex justify-between items-center mb-8">
        <h1 class="text-3xl font-bold text-gray-900 dark:text-gray-100">
          {{ 'clients.title' | translate }}
        </h1>
        <button
          routerLink="/clients/add"
          class="btn-primary"
        >
          <i class="pi pi-plus mr-2"></i>
          {{ 'clients.addClient' | translate }}
        </button>
      </div>

      <div class="card p-6 mb-6">
        <h2 class="text-lg font-semibold mb-4 text-gray-900 dark:text-gray-100">
          {{ 'cars.filters' | translate }}
        </h2>
        
        <form [formGroup]="filterForm" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
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
            <label for="status" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
              {{ 'clients.status' | translate }}
            </label>
            <p-autoComplete
              id="status"
              formControlName="status"
              [suggestions]="filteredStatuses()"
              (completeMethod)="filterStatuses($event)"
              [dropdown]="true"
              optionLabel="label"
              optionValue="value"
              [placeholder]="'clients.status' | translate"
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

          <div class="flex items-end">
            <button
              type="button"
              (click)="clearFilters()"
              class="btn-secondary"
            >
              {{ 'cars.clearFilters' | translate }}
            </button>
          </div>
        </form>
      </div>

      <div class="card">
        <p-table
          [value]="filteredClients()"
          [loading]="store.loading()"
          [paginator]="true"
          [rows]="10"
          [showCurrentPageReport]="true"
          currentPageReportTemplate="Showing {first} to {last} of {totalRecords} clients"
          class="p-datatable-sm"
        >
          <ng-template pTemplate="header">
            <tr>
              <th>{{ 'clients.firstName' | translate }}</th>
              <th>{{ 'clients.lastName' | translate }}</th>
              <th>{{ 'clients.email' | translate }}</th>
              <th>{{ 'clients.phone' | translate }}</th>
              <th>{{ 'clients.credits' | translate }}</th>
              <th>{{ 'clients.status' | translate }}</th>
              <th>{{ 'clients.createdAt' | translate }}</th>
              <th>Actions</th>
            </tr>
          </ng-template>
          
          <ng-template pTemplate="body" let-client>
            <tr class="hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors duration-200">
              <td class="font-medium">{{ client.firstName }}</td>
              <td class="font-medium">{{ client.lastName }}</td>
              <td>{{ client.email }}</td>
              <td>{{ client.phone }}</td>
              <td class="font-semibold text-primary-600 dark:text-primary-400">
                £{{ client.credits }}
              </td>
              <td>
                <p-tag
                  [value]="client.status"
                  [severity]="getStatusSeverity(client.status)"
                />
              </td>
              <td>{{ formatDate(client.createdAt) }}</td>
              <td>
                <div class="flex gap-2">
                  <button
                    [routerLink]="['/clients/edit', client.id]"
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
              <td colspan="9" class="text-center py-8 text-gray-500 dark:text-gray-400">
                No clients found
              </td>
            </tr>
          </ng-template>
        </p-table>
      </div>
    </div>
  `,
})
export class ClientListComponent {
  protected readonly store = inject(AppStore);
  readonly #customerInternalHttpClient = inject(CustomerInternalHttpClient);
  readonly #fb = inject(NonNullableFormBuilder);

  protected readonly filterForm = this.#fb.group({
    search: [''],
    status: [null as UserStatus | null],
  });

  protected readonly statusOptions = Object.values(UserStatus).map(status => ({
    label: status.toUpperCase(),
    value: status,
  }));

  protected readonly filteredStatuses = signal(this.statusOptions);
  protected filterStatuses(event: { query?: string }): void {
    const q = (event.query || '').toLowerCase();
    this.filteredStatuses.set(
      !q ? this.statusOptions : this.statusOptions.filter(o => o.label.toLowerCase().includes(q))
    );
  }

  protected readonly filteredClients = computed(() => {
    const users = this.store.customers();
    const filters = this.filterForm.getRawValue();

    return users.filter(user => {
      const matchesSearch = !filters.search || 
        user.firstName.toLowerCase().includes(filters.search.toLowerCase()) ||
        user.lastName.toLowerCase().includes(filters.search.toLowerCase()) ||
        user.email.toLowerCase().includes(filters.search.toLowerCase());

      const matchesStatus = !filters.status || user.status === filters.status;

      return matchesSearch && matchesStatus;
    });
  });

  constructor() {
    this.#initializeData();
  }

  protected clearFilters(): void {
    this.filterForm.reset();
  }

  protected getStatusSeverity(status: UserStatus): 'success' | 'warning' | 'danger' {
    switch (status) {
      case UserStatus.ACTIVE:
        return 'success';
      case UserStatus.INACTIVE:
        return 'warning';
      case UserStatus.BLOCKED:
        return 'danger';
      default:
        return 'warning';
    }
  }

  protected formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString();
  }

  #initializeData(): void {
    this.#customerInternalHttpClient.getCustomers().subscribe({
        next: (customers) => {
            this.store.setCustomers(customers);
        },
        error: (error) => {
            console.error('Error fetching customers:', error);
        }
    });
  }
}