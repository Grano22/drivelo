package io.github.grano22.carfleetapp.usermanagement.contract;

public record UserDetailsView(
    String id,
    String firstName,
    String lastName,
    String email,
    String phone,
    String birthDate,
    double credits,
    String status,
    String createdAt,
    String updatedAt
) {
}
