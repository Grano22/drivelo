import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-unauthorized',
  standalone: true,
  imports: [CommonModule, RouterModule, TranslateModule, ButtonModule],
  template: `
    <div class="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-gray-900">
      <div class="text-center">
        <div class="mx-auto w-24 h-24 mb-8">
          <div class="bg-error-100 dark:bg-error-900 rounded-full p-6">
            <i class="pi pi-exclamation-triangle text-3xl text-error-600 dark:text-error-400"></i>
          </div>
        </div>
        <h1 class="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-4">
          Unauthorized Access
        </h1>
        <p class="text-gray-600 dark:text-gray-400 mb-8">
          You don't have permission to access this page.
        </p>
        <button routerLink="/dashboard" class="btn-primary">
          Return to Dashboard
        </button>
      </div>
    </div>
  `,
})
export class UnauthorizedComponent {}