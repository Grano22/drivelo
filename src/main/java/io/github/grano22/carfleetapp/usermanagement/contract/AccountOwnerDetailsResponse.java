package io.github.grano22.carfleetapp.usermanagement.contract;

public record AccountOwnerDetailsResponse(
    String firstName,
    String lastName,
    String email,
    String phone,
    String birthDate,
    Integer credits,
    String status,
    String[] roles,
    String[] permissions,
    String createdAt,
    String updatedAt
) {
}
