package io.github.grano22.carfleetapp.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class OneOfEnumConstraintValidator implements ConstraintValidator<OneOfEnum, String> {
    private String[] acceptedNames;

    @Override
    public void initialize(OneOfEnum annotation) {
        Class<? extends Enum<?>> enumClass = annotation.value();
        acceptedNames = Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .toArray(String[]::new);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        return Arrays.asList(acceptedNames).contains(value);
    }
}
