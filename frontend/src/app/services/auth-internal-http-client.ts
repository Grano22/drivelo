import {inject, Injectable} from "@angular/core";
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class AuthInternalHttpClient {
    readonly #http = inject(HttpClient);

    public login(email: string, password: string) {
        const body = new HttpParams()
            .set('username', email)
            .set('password', password);

        const headers = new HttpHeaders({
            'Content-Type': 'application/x-www-form-urlencoded',
        });

        return this.#http.post('/auth/login', body.toString(), { headers });
    }
}