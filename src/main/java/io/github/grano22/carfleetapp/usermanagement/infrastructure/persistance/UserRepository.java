package io.github.grano22.carfleetapp.usermanagement.infrastructure.persistance;

import io.github.grano22.carfleetapp.usermanagement.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
