import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {INTERNAL_API_BASE} from "../constants";
import {User, UserStatus} from "../types/user.types";

export type AddCustomerPayload = {
    firstName: string;
    lastName: string;
    email: string;
    phone: string;
    birthDate: string;
    status: UserStatus;
    password: string;
};

@Injectable({ providedIn: 'root' })
export class CustomerInternalHttpClient {
    readonly #http = inject(HttpClient);

    public addCustomer(payload: AddCustomerPayload) {
        return this.#http.post(INTERNAL_API_BASE + '/user-management/v1/customer/add', payload);
    }
}