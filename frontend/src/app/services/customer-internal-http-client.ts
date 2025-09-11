import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {INTERNAL_API_BASE} from "../constants";
import {CustomerDetailsSchema, UserStatus} from "../types/user.types";
import {switchMap} from "rxjs";

export type AddCustomerPayload = {
    firstName: string;
    lastName: string;
    email: string;
    phone: string;
    birthDate: string;
    status: UserStatus;
    password: string;
    credits?: number;
};

@Injectable({ providedIn: 'root' })
export class CustomerInternalHttpClient {
    readonly #http = inject(HttpClient);

    public addCustomer(payload: AddCustomerPayload) {
        return this.#http.post(INTERNAL_API_BASE + '/user-management/v1/customer/add', payload, { withCredentials: true });
    }

    public editCustomer(customerId: string, payload: Partial<AddCustomerPayload>) {
        return this.#http.put(INTERNAL_API_BASE + `/user-management/v1/customer/edit/${customerId}`, payload, { withCredentials: true });
    }

    public getCustomer(customerId: string) {
        return this.#http.get(INTERNAL_API_BASE + `/user/v1/customer/${customerId}`, { withCredentials: true }).pipe(
            switchMap((rawResponse) => CustomerDetailsSchema.parseAsync(rawResponse))
        );
    }

    public getCustomers() {
        return this.#http.get(INTERNAL_API_BASE + '/user/v1/customers', { withCredentials: true }).pipe(
            switchMap((rawResponse) => CustomerDetailsSchema.array().parseAsync(rawResponse))
        );
    }
}