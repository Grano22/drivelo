package io.github.grano22.carfleetapp.usermanagement.domain;

import lombok.Getter;

import java.util.Set;

@Getter
public enum UserRole {
    ADMIN('*'),
    MANAGER(Set.of(
        UserPermission.ADD_CUSTOMERS,
        UserPermission.MODIFY_CUSTOMERS,
        UserPermission.VIEW_CUSTOMERS,
        UserPermission.ADD_CAR_RENTAL_OFFERS,
        UserPermission.VIEW_CAR_RENTAL_OFFERS,
        UserPermission.MODIFY_CAR_RENTAL_OFFERS
    )),
    CUSTOMER(Set.of(
        UserPermission.VIEW_AVAILABLE_CAR_OFFERS,
        UserPermission.RENT_CARS,
        UserPermission.VIEW_RENTED_CARS,
        UserPermission.RETURN_CAR
    ))
    ;

    private final Set<UserPermission> permissions;

    UserRole(char _wildcard) {
        this.permissions = Set.of(UserPermission.class.getEnumConstants());
    }

    UserRole(Set<UserPermission> permissions) {
        this.permissions = permissions;
    }
}
