import {FormControl, FormGroup} from "@angular/forms";

type PayloadFromForm<T extends Record<string, FormControl<unknown>>> = {
    [K in keyof T]: T[K] extends FormControl<infer V> ? V : never;
};

export const buildPayloadFromChangedFields = <T extends Record<string, FormControl<unknown>>>(formGroup: FormGroup<T>) => {
    const updatedValues: Partial<PayloadFromForm<T>> = {};

    for (const controlName in formGroup.controls) {
        const typedControlName = controlName as keyof T;
        const control = formGroup.controls[typedControlName] as FormControl<unknown> as T[typeof typedControlName];

        type ControlValue = T[typeof typedControlName] extends FormControl<infer V> ? V : never;

        if (control.dirty) {
            updatedValues[typedControlName] = control.value as ControlValue;
        }
    }

    return updatedValues;
}

type NormalizersPerField<T> = Partial<{ [K in keyof T]: (value: Exclude<T[K], undefined>) => unknown }>;

type NormalizedPayload<T, N extends NormalizersPerField<T>> =
    & Omit<T, keyof N>
    & { [K in keyof N & keyof T]: N[K] extends ((arg: T[K]) => infer R) ? R : never }

export const normalizePayload = <T extends Record<string, unknown>, N extends NormalizersPerField<T>>(rawPayload: T, normalizers: N) => {
    const normalizedResult = { ...rawPayload };

    (Object.keys(normalizers) as Array<keyof T & keyof N>).forEach((k) => {
        const fn = normalizers[k] as ((value: T[typeof k]) => unknown) | undefined;

        const potentialValue = rawPayload[k as keyof T];
        if (fn && typeof fn === 'function' && potentialValue !== undefined) {
            normalizedResult[k] = fn(potentialValue as Exclude<T[typeof k], undefined>) as NormalizedPayload<T, N>[typeof k];
        }
    });

    return normalizedResult as NormalizedPayload<T, N>;
}