package io.github.grano22.carfleetapp.usermanagement.contract;

public record UserDetailsView(
    String firstName,
    String lastName,
    String email,
    String phone,
    String birthDate,
    Integer credits,
    String status,
    String createdAt,
    String updatedAt
) {
}
