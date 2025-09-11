import {Component, computed, effect, inject, signal} from "@angular/core";
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {SaveCarDetails, SaveCarDetailsFormGroup, SaveCarForm} from "./save-car-form";
import {TranslatePipe} from "@ngx-translate/core";
import {AppStore} from "../../store/app.store";
import {toSignal} from "@angular/core/rxjs-interop";
import {Button} from "primeng/button";
import {Message} from "primeng/message";
import {
    CarFleetInternalHttpClients,
    UpdateCarWithRentalOfferPayload
} from "../../services/car-fleet-internal-http-clients";
import {CarRentalOffer, CarStatus} from "../../types/car.types";
import {buildPayloadFromChangedFields, filterAutocomplete, normalizePayload} from "../../form/form.utils";
import {ProgressSpinner} from "primeng/progressspinner";
import {AutoComplete} from "primeng/autocomplete";
import {FormsModule, NonNullableFormBuilder, ReactiveFormsModule, Validators} from "@angular/forms";
import {PrimeTemplate} from "primeng/api";
import {CarRentalOfferStatus} from "../../types/car-rental-offer.types";

@Component({
    standalone: true,
    imports: [
        RouterLink,
        SaveCarForm,
        TranslatePipe,
        Button,
        Message,
        ProgressSpinner,
        AutoComplete,
        FormsModule,
        PrimeTemplate,
        ReactiveFormsModule
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
                    {{ 'cars.editCar' | translate }}: {{ carId() }}
                </h1>
            </div>

            <div class="card p-8">
                @defer (when apiLoaded()) {
                    @if (lastApiError()) {
                        <p-message severity="error">Could not load the form. Please try again.</p-message>
                        <p-button (click)="retry()">Retry</p-button>
                    } @else {
                        <div class="flex space-x-4 pb-6 mb-6 border-b border-gray-200 dark:border-gray-700">
                            <form [formGroup]="statusesForm" class="space-y-8">
                                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                                    <div>
                                        <label for="vehicleType" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                                            {{ 'cars.offerStatus' | translate }} *
                                        </label>
                                        <p-autoComplete
                                                id="vehicleType"
                                                formControlName="carOfferStatus"
                                                [suggestions]="filteredCarOfferStatuses()"
                                                (completeMethod)="filterAutocomplete($event, filteredCarOfferStatuses, carOfferStatuses)"
                                                [dropdown]="true"
                                                optionLabel="label"
                                                optionValue="value"
                                                [placeholder]="'cars.offerStatus' | translate"
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
                                        <label for="vehicleType" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                                            {{ 'cars.carStatus' | translate }} *
                                        </label>
                                        <p-autoComplete
                                                id="vehicleType"
                                                formControlName="carStatus"
                                                [suggestions]="filteredCarStatuses()"
                                                (completeMethod)="filterAutocomplete($event, filteredCarStatuses, carStatuses)"
                                                [dropdown]="true"
                                                optionLabel="label"
                                                optionValue="value"
                                                [placeholder]="'cars.carStatus' | translate"
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
                                </div>
                            </form>
                        </div>
                        
                        <app-save-car-form
                            [initialValue]="carWithOfferDetails()!"
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
export class EditCarComponent {
    protected readonly store = inject(AppStore);
    readonly #carFleetInternalHttpClients = inject(CarFleetInternalHttpClients);
    readonly #router = inject(Router);
    readonly #fb = inject(NonNullableFormBuilder);

    protected carId = signal<string | null>(null);
    private activatedRoute = inject(ActivatedRoute);
    readonly routeParams = toSignal(this.activatedRoute.params, { initialValue: { id: null } });

    protected readonly loading = signal(false);
    protected readonly errorMessage = signal<string | null>(null);

    protected apiLoaded = signal(false);
    protected lastApiError = signal<string | null>(null);
    protected fetchedCarWithOfferDetails = signal<CarRentalOffer|null>(null);
    protected carWithOfferDetails = computed<Partial<SaveCarDetails>|null>(() => {
        const fetchedCarWithOfferDetails = this.fetchedCarWithOfferDetails();

        if (!fetchedCarWithOfferDetails) {
            return null;
        }

        return normalizePayload(fetchedCarWithOfferDetails, {
            imageUrl: (imageUrl) => imageUrl,
            maxRentalDays: (maxRentalDays) => maxRentalDays,
        }) satisfies SaveCarDetails;
    });

    protected readonly carStatuses = Object.values(CarStatus).map(type => ({
        label: type.toUpperCase().replace('_', ' '),
        value: type,
    }));
    protected readonly carOfferStatuses = [
        CarRentalOfferStatus.ACTIVE,
        CarRentalOfferStatus.DISABLED,
        CarRentalOfferStatus.ARCHIVED
    ].map(type => ({
        label: type.toUpperCase().replace('_', ' '),
        value: type,
    }));

    protected readonly filteredCarStatuses = signal(this.carStatuses);
    protected readonly filteredCarOfferStatuses = signal(this.carOfferStatuses);

    protected statusesForm = this.#fb.group({
        carStatus: [CarStatus.AVAILABLE, [Validators.required]],
        carOfferStatus: [CarRentalOfferStatus.ACTIVE, [Validators.required]],
    });

    constructor() {
        effect(() => {
            const params = this.routeParams();
            this.apiLoaded.set(false);
            this.carId.set(params['id']);
        });
        effect(() => {
            const carId = this.carId();

            if (!carId) {
                return;
            }

            this.#fetchCarDetails(carId);
        });
        effect(() => {
            const data = this.fetchedCarWithOfferDetails();
            this.statusesForm = this.#fb.group({
                carStatus: [data?.carStatus || CarStatus.AVAILABLE, [Validators.required]],
                carOfferStatus: [data?.status || CarRentalOfferStatus.ACTIVE, [Validators.required]],
            });
        });
    }

    protected async onSubmit(carForm: SaveCarDetailsFormGroup): Promise<void> {
        if (!carForm.dirty && !this.statusesForm.dirty) {
            return;
        }

        if (carForm.invalid) {
            carForm.markAllAsTouched();
            return;
        }

        if (this.statusesForm.invalid) {
            this.statusesForm.markAllAsTouched();
            return;
        }

        this.loading.set(true);
        this.errorMessage.set(null);

        try {
            const updatedValues = buildPayloadFromChangedFields(carForm);
            const updatedStatuses = this.statusesForm.getRawValue();
            const previouslyFetchedDetails = this.fetchedCarWithOfferDetails();
            const normalizedValues = {
                car: {
                    name: updatedValues.brand || updatedValues.model ? `${updatedValues.brand || previouslyFetchedDetails?.brand} ${updatedValues.model || previouslyFetchedDetails?.model}` : undefined,
                    brand: updatedValues.brand,
                    model: updatedValues.model,
                    year: updatedValues.year,
                    type: updatedValues.vehicleType!,
                    maxCapacity: updatedValues.maxCapacity,
                    status: updatedStatuses.carStatus,
                    gearboxType: updatedValues.gearboxType || undefined,
                    engineType: updatedValues.engineType || undefined,
                    amenities: updatedValues.amenities,
                    imageUrl: updatedValues.imageUrl || undefined,
                    mileage: updatedValues.mileage,
                    fuelConsumption: updatedValues.fuelConsumption
                },
                rentalOfferDetails: {
                    pricePerDay: updatedValues.pricePerDay,
                    maxRentalDays: updatedValues.maxRentalDays,
                    description: updatedValues.description,
                    status: updatedStatuses.carOfferStatus,
                }
            } satisfies Partial<UpdateCarWithRentalOfferPayload>;

            this.#carFleetInternalHttpClients.updateCarWithRentalOffer(this.carId()!, normalizedValues).subscribe({
                next: async () => {
                    this.loading.set(false);
                    await this.#router.navigate(['/fleet-management']);
                },
                error: (error) => {
                    this.loading.set(false);
                    throw Error('Error creating client:' + error.message);
                }
            });
        } catch (error) {
            this.errorMessage.set('Failed to update car with offer. Please check all fields.');
        }
    }

    protected retry() {
        this.apiLoaded.set(false);
        this.#fetchCarDetails(this.carId()!);
    }

    protected readonly filterAutocomplete = filterAutocomplete;

    #fetchCarDetails(carId: string) {
        this.#carFleetInternalHttpClients.getCarWithRentalOffer(carId).subscribe({
            next: (customer) => {
                this.fetchedCarWithOfferDetails.set(customer);
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