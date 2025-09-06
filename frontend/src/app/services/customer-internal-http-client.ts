import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {INTERNAL_API_BASE} from "../constants";
import {User} from "../types/user.types";

export type AddCustomerPayload = {
    firstName: string;
    lastName: string;
    email: string;
    phone: string;
    birthDate: string;
    address: string;
    status: 'active' | 'inactive' | 'blocked';
};

@Injectable({ providedIn: 'root' })
class CustomerInternalHttpClient {
    readonly #http = inject(HttpClient);

    public addCustomer(payload: AddCustomerPayload) {
        this.#http.post(INTERNAL_API_BASE + '/user-management/v1', payload);
    }
}