package io.github.grano22.carfleetapp.usermanagement.infrastructure.persistance;

import io.github.grano22.carfleetapp.usermanagement.User;
import io.github.grano22.carfleetapp.usermanagement.domain.UserRole;
import io.github.grano22.carfleetapp.usermanagement.domain.UserStatus;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@Profile("dev")
public class DevDbFeeder {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DevDbFeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional(propagation = Propagation.REQUIRED)
    public void seed() {
        UUID adminUUID = UUID.fromString("d7e88a51-fa02-4342-8115-ef389fd678f8");
        UUID managerUUID = UUID.fromString("d7e88a51-fa02-4342-8115-ef389fd678f9");
        UUID customerUUID = UUID.fromString("d7e88a51-fa02-4342-8115-ef389fd678fa");
        User admin = User.builder()
            .id(adminUUID)
            .firstName("Admin")
            .lastName("Adminov")
            .email("admin@drivelo.org")
            .password(passwordEncoder.encode("iosa8i676fJYT&Iu6dd"))
            .phone("123 123 123")
            .birthDate(LocalDate.of(2022, 1, 1))
            .credits(new BigDecimal(100))
            .status(UserStatus.ACTIVE)
            .roles(Set.of(UserRole.ADMIN))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build()
        ;
        User manager = User.builder()
            .id(managerUUID)
            .firstName("Stefan")
            .lastName("Stasiak")
            .email("manager@drivelo.org")
            .password(passwordEncoder.encode("iiysaiuuIYT&^d5sd"))
            .phone("321 321 321")
            .birthDate(LocalDate.of(2023, 1, 1))
            .credits(new BigDecimal(100))
            .status(UserStatus.ACTIVE)
            .roles(Set.of(UserRole.MANAGER))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build()
        ;
        User customer = User.builder()
            .id(customerUUID)
            .firstName("Maciek")
            .lastName("Witnuk")
            .email("customer@drivelo.org")
            .password(passwordEncoder.encode("os7dy9dssdKHahdk"))
            .phone("231 231 231")
            .birthDate(LocalDate.of(2022, 1, 1))
            .credits(new BigDecimal(1000))
            .status(UserStatus.ACTIVE)
            .roles(Set.of(UserRole.CUSTOMER))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build()
        ;

        for (var user: List.of(admin, manager, customer)) {
            if (!userRepository.existsById(user.getId())) {
                userRepository.save(user);
            }
        }
    }
}
