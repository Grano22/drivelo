import {Component, effect, inject} from '@angular/core';
import {CommonModule} from '@angular/common';
import {provideRouter, RouterOutlet} from '@angular/router';
import {provideTranslateService, TranslateLoader, TranslateModule, TranslateService} from '@ngx-translate/core';
import {bootstrapApplication} from '@angular/platform-browser';
import {provideAnimations} from '@angular/platform-browser/animations';

import {routes} from './app/app.routes';
import {AppStore} from './app/store/app.store';
import {AuthService} from './app/services/auth.service';
import {HeaderComponent} from './app/components/layout/header.component';
import {PreloadedTranslateLoader} from "./app/i18n/PreloadedTranslateLoader";
import {TRANSLATIONS} from "./app/i18n/translations";
import {providePrimeNG} from "primeng/config";
import Aura from '@primeuix/themes/aura';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, TranslateModule, HeaderComponent],
  template: `
    <div class="min-h-screen bg-gray-50 dark:bg-gray-900 transition-colors duration-200">
      <app-header />
      <main>
        <router-outlet />
      </main>
    </div>
  `,
})
export class App {
  readonly #store = inject(AppStore);
  readonly #authService = inject(AuthService);
  readonly #translateService = inject(TranslateService);

  constructor() {
      effect(() => {
          this.#store.initializeTheme();
          this.#store.initializeLanguage();

          this.#translateService.setFallbackLang('en-GB');
          this.#translateService.use(this.#store.language());

          this.#authService.checkAutoLogin();
      });
  }
}

void bootstrapApplication(App, {
  providers: [
    provideRouter(routes),
    provideAnimations(),
    providePrimeNG({
        theme: {
            preset: Aura
        }
    }),
    provideTranslateService({
        loader: {
            provide: TranslateLoader,
            useFactory: () => {
                return new PreloadedTranslateLoader(TRANSLATIONS);
            }
        },
        fallbackLang: 'en-GB'
    })
  ],
});