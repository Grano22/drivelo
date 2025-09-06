package io.github.grano22.carfleetapp.usermanagement.infrastructure.persistance;

import io.github.grano22.carfleetapp.usermanagement.User;
import io.github.grano22.carfleetapp.usermanagement.domain.UserRole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r in :roles")
    Collection<User> findByRoles(@Param("roles") Set<UserRole> roles);
}
