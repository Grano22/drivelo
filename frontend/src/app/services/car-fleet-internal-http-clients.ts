import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {switchMap} from "rxjs";
import {AmenityType, CarRentalOffersSchema, CarStatus, EngineType, GearboxType, VehicleType} from "../types/car.types";
import {INTERNAL_API_BASE} from "../constants";
import {CarRentalOfferStatus} from "../types/car-rental-offer.types";

type AddCarPayload = {
    name: string;
    brand: string;
    model?: string;
    year?: number;
    type: VehicleType;
    maxCapacity: number;
    status: CarStatus;
    gearboxType: GearboxType;
    engineType: EngineType;
    amenities: AmenityType[];
    imageUrl?: string;
    mileage?: number;
    fuelConsumption?: number;
    description: string;
}

type AddCarRentalOfferPayload = {
    pricePerDay: number;
    minRentalDays?: number;
    maxRentalDays?: number;
    status: CarRentalOfferStatus;
    description: string;
}

export type AddCarWithRentalOfferPayload = {
    car: AddCarPayload;
    rentalOfferDetails: AddCarRentalOfferPayload;
};

@Injectable({ providedIn: 'root' })
export class CarFleetInternalHttpClients {
    readonly #http = inject(HttpClient);

    public getCarRentalOffers() {
        return this.#http.get(INTERNAL_API_BASE + '/car-fleet/v1', { withCredentials: true }).pipe(
            switchMap(rawResponse => {
                return CarRentalOffersSchema.parseAsync(rawResponse);
            })
        );
    }

    public addCarWithRentalOffer(payload: AddCarWithRentalOfferPayload) {
        return this.#http.post(INTERNAL_API_BASE + '/car-fleet-management/v1/car-with-rental-offer/save/', payload, { withCredentials: true });
    }

    public getCarsForRent() {
        return this.#http.get(INTERNAL_API_BASE + '/car-fleet/v1/for_rent', { withCredentials: true }).pipe(
            switchMap(rawResponse => {
                return CarRentalOffersSchema.parseAsync(rawResponse);
            })
        );
    }

    public rentCar(offerId: string, from: Date, to: Date) {
        return this.#http.post(INTERNAL_API_BASE + '/car-rental/v1/rent', { offerId, from, to }, { withCredentials: true});
    }

    public returnCar(rentalId: string) {
        return this.#http.post(INTERNAL_API_BASE + '/car-rental/v1/return', { rentalId }, { withCredentials: true});
    }
}