package io.github.grano22.carfleetapp.usermanagement.actions;

import io.github.grano22.carfleetapp.usermanagement.infrastructure.persistance.UserRepository;
import io.github.grano22.carfleetapp.usermanagement.User;
import io.github.grano22.carfleetapp.usermanagement.contract.AddCustomerRequest;
import io.github.grano22.carfleetapp.usermanagement.domain.UserRole;
import lombok.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
public class AddCustomerUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    public AddCustomerUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder, Clock clock) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
    }

    public void execute(@NonNull AddCustomerRequest request) {
        LocalDateTime now = LocalDateTime.now(clock);
        User newCustomer = User.builder()
            .id(UUID.randomUUID())
            .password(passwordEncoder.encode(request.password()))
            .status(request.status())
            .firstName(request.firstName())
            .lastName(request.lastName())
            .email(request.email())
            .phone(request.phone())
            .birthDate(request.birthDate())
            .roles(Set.of(UserRole.CUSTOMER))
            .createdAt(now)
            .updatedAt(now)
            .build()
        ;

        this.userRepository.save(newCustomer);
    }
}
