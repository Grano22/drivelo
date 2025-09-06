import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {INTERNAL_API_BASE} from "../constants";
import {switchMap} from "rxjs";
import {UserSchema} from "../types/user.types";

@Injectable({ providedIn: 'root' })
export class UserDetailsInternalHttpClient {
    readonly #http = inject(HttpClient);

    public getCurrentUserDetails() {
        return this.#http.get<unknown>(INTERNAL_API_BASE + '/user/v1', { withCredentials: true }).pipe(
            switchMap(rawResponse => {
                return UserSchema.parseAsync(rawResponse);
            })
        );
    }
}