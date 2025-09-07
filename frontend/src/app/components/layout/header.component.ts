import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { ButtonModule } from 'primeng/button';
import { MenuModule } from 'primeng/menu';
import { PopoverModule } from 'primeng/popover';
import { AuthService } from '../../services/auth.service';
import { AppStore } from '../../store/app.store';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule, TranslateModule, ButtonModule, MenuModule, PopoverModule],
  template: `
    <header class="bg-white dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700 sticky top-0 z-50">
      <nav class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
          <div class="flex items-center space-x-8">
            <div class="flex-shrink-0">
              <h1 class="text-xl font-bold text-primary-600 dark:text-primary-400">
                {{ 'app.title' | translate }}
              </h1>
            </div>
            
            @if (authService.isAuthenticated()) {
              <div class="hidden md:block">
                <div class="flex items-baseline space-x-4">
                    <a
                        routerLink="/dashboard"
                        routerLinkActive="text-primary-600 dark:text-primary-400"
                        class="text-gray-600 dark:text-gray-300 hover:text-primary-600 dark:hover:text-primary-400 px-3 py-2 rounded-md text-sm font-medium transition-colors duration-200"
                    >
                        {{ 'navigation.dashboard' | translate }}
                    </a>
                    
                  @if (store.isCustomer()) {
                    <a 
                      routerLink="/cars"
                      routerLinkActive="text-primary-600 dark:text-primary-400"
                      class="text-gray-600 dark:text-gray-300 hover:text-primary-600 dark:hover:text-primary-400 px-3 py-2 rounded-md text-sm font-medium transition-colors duration-200"
                    >
                      {{ 'navigation.cars' | translate }}
                    </a>
                  }
                  
                  @if (store.isManager()) {
                    <a 
                      routerLink="/clients"
                      routerLinkActive="text-primary-600 dark:text-primary-400"
                      class="text-gray-600 dark:text-gray-300 hover:text-primary-600 dark:hover:text-primary-400 px-3 py-2 rounded-md text-sm font-medium transition-colors duration-200"
                    >
                      {{ 'navigation.clients' | translate }}
                    </a>
                    <a 
                      routerLink="/fleet-management"
                      routerLinkActive="text-primary-600 dark:text-primary-400"
                      class="text-gray-600 dark:text-gray-300 hover:text-primary-600 dark:hover:text-primary-400 px-3 py-2 rounded-md text-sm font-medium transition-colors duration-200"
                    >
                      Fleet Management
                    </a>
                  }
                </div>
              </div>
            }
          </div>

          <div class="flex items-center space-x-4">
            <button
              (click)="toggleTheme()"
              class="p-2 text-gray-600 dark:text-gray-300 hover:text-primary-600 dark:hover:text-primary-400 transition-colors duration-200"
              [attr.aria-label]="store.theme() === 'dark' ? 'Switch to light mode' : 'Switch to dark mode'"
            >
              @if (store.theme() === 'dark') {
                <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M10 2a1 1 0 011 1v1a1 1 0 11-2 0V3a1 1 0 011-1zm4 8a4 4 0 11-8 0 4 4 0 018 0zm-.464 4.95l.707.707a1 1 0 001.414-1.414l-.707-.707a1 1 0 00-1.414 1.414zm2.12-10.607a1 1 0 010 1.414l-.706.707a1 1 0 11-1.414-1.414l.707-.707a1 1 0 011.414 0zM17 11a1 1 0 100-2h-1a1 1 0 100 2h1zm-7 4a1 1 0 011 1v1a1 1 0 11-2 0v-1a1 1 0 011-1zM5.05 6.464A1 1 0 106.465 5.05l-.708-.707a1 1 0 00-1.414 1.414l.707.707zm1.414 8.486l-.707.707a1 1 0 01-1.414-1.414l.707-.707a1 1 0 011.414 1.414zM4 11a1 1 0 100-2H3a1 1 0 000 2h1z" clip-rule="evenodd" />
                </svg>
              } @else {
                <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M17.293 13.293A8 8 0 016.707 2.707a8.001 8.001 0 1010.586 10.586z" />
                </svg>
              }
            </button>

            <select
              (change)="changeLanguage($event)"
              [value]="store.language()"
              class="bg-white dark:bg-gray-800 border border-gray-300 dark:border-gray-600 rounded-md px-3 py-1 text-sm"
            >
              <option value="en-GB">EN (UK)</option>
              <option value="en-US">EN (US)</option>
              <option value="pl-PL">PL</option>
            </select>

            @if (authService.isAuthenticated()) {
              <div class="flex items-center space-x-2">
                <span class="text-sm text-gray-600 dark:text-gray-300">
                  {{ store.currentUser()?.firstName }} {{ store.currentUser()?.lastName }}
                </span>
                <button
                  (click)="authService.logout()"
                  class="btn-secondary text-sm py-1 px-3"
                >
                  {{ 'auth.logout' | translate }}
                </button>
              </div>
            }
          </div>
        </div>
      </nav>
    </header>
  `,
})
export class HeaderComponent {
  protected readonly authService = inject(AuthService);
  protected readonly store = inject(AppStore);
  readonly #translateService = inject(TranslateService);

  protected toggleTheme(): void {
    const newTheme = this.store.theme() === 'light' ? 'dark' : 'light';
    this.store.setTheme(newTheme);
  }

  protected changeLanguage(event: Event): void {
    const target = event.target as HTMLSelectElement;
    const language = target.value as 'en-GB' | 'en-US' | 'pl-PL';
    this.store.setLanguage(language);
    this.#translateService.use(language);
  }
}