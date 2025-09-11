package io.github.grano22.carfleetapp.usermanagement.domain;

import io.github.grano22.carfleetapp.shared.domain.InvalidDataGivenForOperation;

import java.util.UUID;

public class MissingRequestedUser extends InvalidDataGivenForOperation {
    public MissingRequestedUser(UUID userId) {
        super("User " + userId + " not found");
    }
}
