import {FormControl} from "@angular/forms";

export type RecursiveRecord = { [key: string]: string | RecursiveRecord; };
export type FormControlsOf<T> = { [key in keyof T]: FormControl<T[key]> };