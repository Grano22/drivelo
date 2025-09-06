package io.github.grano22.carfleetapp.usermanagement.contract;

public record UserDetailsResponse(
    String firstName,
    String lastName,
    String email,
    String phone,
    String birthDate,
    String address,
    String status,
    String[] roles
) {
}
