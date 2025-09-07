package io.github.grano22.carfleetapp.usermanagement.contract;

import io.github.grano22.carfleetapp.usermanagement.domain.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AddCustomerRequest(
    @NotNull String firstName,
    @NotNull String lastName,
    @NotNull @Size(min = 8, max = 32) String password,
    @NotNull @Email String email,
    String phone,
    @NotNull @Past LocalDate birthDate,
    @NotNull UserStatus status,
    Double credits
) {
}
