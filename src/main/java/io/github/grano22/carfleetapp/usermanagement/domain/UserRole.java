package io.github.grano22.carfleetapp.usermanagement.domain;

import lombok.Getter;

import java.util.Set;

@Getter
public enum UserRole {
    ADMIN('*'),
    MANAGER(Set.of(UserPermission.ADD_CUSTOMERS, UserPermission.VIEW_CUSTOMERS)),
    CUSTOMER(Set.of(UserPermission.VIEW_OFFERS, UserPermission.RENT_CARS))
    ;

    private final Set<UserPermission> permissions;

    UserRole(char _wildcard) {
        this.permissions = Set.of(UserPermission.class.getEnumConstants());
    }

    UserRole(Set<UserPermission> permissions) {
        this.permissions = permissions;
    }
}
