import {Component, computed, effect, inject, signal} from "@angular/core";
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {SaveClientDetailsForm, SaveClientDetailsFormGroup} from "./save-client-details-form";
import {TranslatePipe} from "@ngx-translate/core";
import {AppStore} from "../../store/app.store";
import {AddCustomerPayload, CustomerInternalHttpClient} from "../../services/customer-internal-http-client";
import {toSignal} from "@angular/core/rxjs-interop";
import {buildPayloadFromChangedFields, normalizePayload} from "../../form/form.utils";
import {ProgressSpinner} from "primeng/progressspinner";
import {Customer} from "../../types/user.types";
import {Message} from "primeng/message";
import {Button} from "primeng/button";

@Component({
    standalone: true,
    imports: [
        RouterLink,
        SaveClientDetailsForm,
        TranslatePipe,
        ProgressSpinner,
        Message,
        Button
    ],
    template: `
        <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <div class="mb-8">
                <button
                    routerLink="/clients"
                    class="btn-secondary mb-4"
                >
                    <i class="pi pi-arrow-left mr-2"></i>
                    {{ 'app.back' | translate }}
                </button>

                <h1 class="text-3xl font-bold text-gray-900 dark:text-gray-100">
                    {{ 'clients.editClient' | translate }}: {{ customerId() }}
                </h1>
            </div>

            <div class="card p-8">
                @defer (when apiLoaded()) {
                    @if (lastApiError()) {
                        <p-message severity="error">Could not load the form. Please try again.</p-message>
                        <p-button (click)="retry()">Retry</p-button>
                    } @else {
                        <app-save-client-details-form
                             [initialValue]="customerDetails()!"
                             validationGroup="edit"
                             [loading]="loading()"
                             [errorMessage]="errorMessage()"
                             (onSubmit)="onSubmit($event)"
                        />
                    }
                }
                @placeholder {
                    <div>Preparing data...</div>
                }
                @loading {
                    <p-progressSpinner/>
                }
            </div>
        </div>
    `,
})
export class EditClientComponent {
    protected readonly store = inject(AppStore);
    readonly #customerInternalHttpClient = inject(CustomerInternalHttpClient);
    readonly #router = inject(Router);

    protected customerId = signal<string | null>(null);
    private activatedRoute = inject(ActivatedRoute);
    readonly routeParams = toSignal(this.activatedRoute.params, { initialValue: { id: null } });

    protected readonly loading = signal(false);
    protected readonly errorMessage = signal<string | null>(null);

    protected apiLoaded = signal(false);
    protected lastApiError = signal<string | null>(null);
    protected fetchedCustomerDetails = signal<Customer|null>(null);
    protected customerDetails = computed(() => {
        const fetchedCustomerDetails = this.fetchedCustomerDetails();

        if (!fetchedCustomerDetails) {
            return null;
        }

        return { ...fetchedCustomerDetails, birthDate: new Date(Date.parse(fetchedCustomerDetails.birthDate)) };
    });

    constructor() {
        effect(() => {
            const params = this.routeParams();
            this.apiLoaded.set(false);
            this.customerId.set(params['id']);
        });
        effect(() => {
            const customerId = this.customerId();

            if (!customerId) {
                return;
            }

            this.#fetchCustomerDetails(customerId);
        });
    }

    protected async onSubmit(clientForm: SaveClientDetailsFormGroup): Promise<void> {
        if (!clientForm.dirty) {
            return;
        }

        if (clientForm.invalid) {
            clientForm.markAllAsTouched();
            return;
        }

        this.loading.set(true);
        this.errorMessage.set(null);

        try {
            const updatedValues = buildPayloadFromChangedFields(clientForm);
            const normalizedValues = normalizePayload(updatedValues, {
                birthDate: (birthDate) => birthDate?.toISOString().split('T')[0]
            }) satisfies Partial<AddCustomerPayload>;

            this.#customerInternalHttpClient.editCustomer(this.customerId()!, normalizedValues).subscribe({
                next: async () => {
                    this.loading.set(false);
                    await this.#router.navigate(['/clients']);
                },
                error: (error) => {
                    this.loading.set(false);
                    throw Error('Error creating client:' + error.message);
                }
            });
        } catch (error) {
            this.errorMessage.set('Failed to update client. Please check all fields.');
        }
    }

    protected retry() {
        this.apiLoaded.set(false);
        this.#fetchCustomerDetails(this.customerId()!);
    }

    #fetchCustomerDetails(customerId: string) {
        this.#customerInternalHttpClient.getCustomer(customerId).subscribe({
            next: (customer) => {
                this.fetchedCustomerDetails.set(customer);
                this.apiLoaded.set(true);
            },
            error: (error) => {
                this.lastApiError.set(error.message);
                this.apiLoaded.set(true);
                console.error('Error fetching client details:', error);
            }
        });
    }
}