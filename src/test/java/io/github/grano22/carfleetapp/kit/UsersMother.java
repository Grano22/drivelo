package io.github.grano22.carfleetapp.kit;

import io.github.grano22.carfleetapp.usermanagement.User;
import io.github.grano22.carfleetapp.usermanagement.domain.UserRole;
import io.github.grano22.carfleetapp.usermanagement.domain.UserStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class UsersMother {
    public final static String ADMIN_UUID = "f9415b6f-53b5-4fea-adea-4e11f4c0cb46";
    public final static String ADMIN_EMAIL = "admin@drivelo.org";
    public final static String ADMIN_PASSWORD = "%HD&H65s7KFGGjdsgkjkgdsua";

    public final static String MANAGER_UUID = "f9415b6f-53b5-4fea-adea-4e11f4c0cb47";
    public final static String MANAGER_EMAIL = "manager@drivelo.org";
    public final static String MANAGER_PASSWORD = "OPDFYUou4535";

    public final static String CUSTOMER_UUID = "f9415b6f-53b5-4fea-adea-4e11f4c0cb48";
    public final static String CUSTOMER_EMAIL = "customer@drivelo.org";
    public final static String CUSTOMER_PASSWORD = "OPDFYUou4534";

    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();

    public static User bornAdmin() {
        return new User(
            UUID.fromString(ADMIN_UUID),
            "Admin",
            "Root",
            ADMIN_EMAIL,
            encoder.encode(ADMIN_PASSWORD),
            "123 123 123",
            LocalDate.of(2000, 1, 1),
            UserStatus.ACTIVE,
            Set.of(UserRole.ADMIN),
            LocalDateTime.of(2023, 1, 1, 1, 1),
            LocalDateTime.of(2023, 1, 1, 1, 1)
        );
    }

    public static User bornManager() {
        return new User(
            UUID.fromString(MANAGER_UUID),
            "Stefan",
            "Vilvo",
            MANAGER_EMAIL,
            encoder.encode(MANAGER_PASSWORD),
            "321 321 321",
            LocalDate.of(2000, 1, 1),
            UserStatus.ACTIVE,
            Set.of(UserRole.MANAGER),
            LocalDateTime.of(2024, 1, 1, 1, 1),
            LocalDateTime.of(2024, 1, 1, 1, 1)
        );
    }

    public static User bornCustomer() {
        return new User(
            UUID.fromString(CUSTOMER_UUID),
            "Martin",
            "Lee",
            CUSTOMER_EMAIL,
            encoder.encode(CUSTOMER_PASSWORD),
            "213 213 213",
            LocalDate.of(2000, 1, 1),
            UserStatus.ACTIVE,
            Set.of(UserRole.CUSTOMER),
            LocalDateTime.of(2025, 1, 1, 1, 1),
            LocalDateTime.of(2025, 1, 1, 1, 1)
        );
    }
}
