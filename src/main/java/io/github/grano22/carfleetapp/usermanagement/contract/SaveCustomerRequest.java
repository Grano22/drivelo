package io.github.grano22.carfleetapp.usermanagement.contract;

import io.github.grano22.carfleetapp.shared.validation.OnCreate;
import io.github.grano22.carfleetapp.usermanagement.domain.UserStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SaveCustomerRequest(
    @NotNull(groups = OnCreate.class) String firstName,
    @NotNull(groups = OnCreate.class) String lastName,
    @NotNull(groups = OnCreate.class) @Size(min = 8, max = 32) String password,
    @NotNull(groups = OnCreate.class) @Email String email,
    String phone,
    @NotNull(groups = OnCreate.class) @Past LocalDate birthDate,
    @NotNull(groups = OnCreate.class) UserStatus status,
    @Digits(integer = 8, fraction = 2) BigDecimal credits
) {
}
