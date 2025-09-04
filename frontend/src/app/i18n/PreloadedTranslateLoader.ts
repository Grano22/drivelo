import { Observable, of } from 'rxjs';
import { TranslateLoader } from '@ngx-translate/core';
import {RecursiveRecord} from "../types/utility.types";

export class PreloadedTranslateLoader implements TranslateLoader {
    readonly #translations: RecursiveRecord;

    constructor(translations: RecursiveRecord) {
        this.#translations = translations;
    }

    getTranslation(lang: string): Observable<any> {
        return of(this.#translations[lang] ?? {});
    }
}
