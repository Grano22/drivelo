package io.github.grano22.carfleetapp.kit;

import io.github.grano22.carfleetapp.usermanagement.User;
import io.github.grano22.carfleetapp.usermanagement.domain.UserRole;
import io.github.grano22.carfleetapp.usermanagement.domain.UserStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class UsersMother {
    public final static String ADMIN_UUID = "f9415b6f-53b5-4fea-adea-4e11f4c0cb46";
    public final static String ADMIN_EMAIL = "admin@example.org";
    public final static String ADMIN_PASSWORD = "%HD&H65s7KFGGjdsgkjkgdsua";

    public final static String MANAGER_UUID = "f9415b6f-53b5-4fea-adea-4e11f4c0cb47";
    public final static String MANAGER_EMAIL = "manager@example.org";
    public final static String MANAGER_PASSWORD = "OPDFYUou4535";

    public final static String CUSTOMER_UUID = "f9415b6f-53b5-4fea-adea-4e11f4c0cb48";
    public final static String CUSTOMER_EMAIL = "customer@example.org";
    public final static String CUSTOMER_PASSWORD = "OPDFYUou4534";

    public final static String SECOND_CUSTOMER_UUID = "f9415b6f-53b5-4fea-adea-4e11f4c0cb49";
    public final static String SECOND_CUSTOMER_EMAIL = "customer.second@example.org";
    public final static String SECOND_CUSTOMER_PASSWORD = "iY^OD87fdd";

    public final static String INACTIVE_CUSTOMER_UUID = "f9415b6f-53b5-4fea-adea-4e11f4c0cb4A";
    public final static String INACTIVE_CUSTOMER_EMAIL = "customer.inactive@example.org";
    public final static String INACTIVE_CUSTOMER_PASSWORD = "O&DY8768f7d";

    public final static String BLOCKED_CUSTOMER_UUID = "f9415b6f-53b5-4fea-adea-4e11f4c0cb4B";
    public final static String BLOCKED_CUSTOMER_EMAIL = "customer.blocked@example.org";
    public final static String BLOCKED_CUSTOMER_PASSWORD = "oYFD8i7dfy6k";

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
            BigDecimal.valueOf(100D),
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
            BigDecimal.valueOf(100D),
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
            BigDecimal.valueOf(300D),
            UserStatus.ACTIVE,
            Set.of(UserRole.CUSTOMER),
            LocalDateTime.of(2025, 1, 1, 1, 1),
            LocalDateTime.of(2025, 1, 1, 1, 1)
        );
    }

    public static User bornSecondCustomer() {
        return new User(
            UUID.fromString(SECOND_CUSTOMER_UUID),
            "Monica",
            "luvour",
            SECOND_CUSTOMER_EMAIL,
            encoder.encode(SECOND_CUSTOMER_PASSWORD),
            "111 111 111",
            LocalDate.of(1996, 1, 1),
            BigDecimal.valueOf(0D),
            UserStatus.ACTIVE,
            Set.of(UserRole.CUSTOMER),
            LocalDateTime.of(2020, 1, 1, 1, 1),
            LocalDateTime.of(2020, 1, 1, 1, 1)
        );
    }

    public static User bornInactiveCustomer() {
        return new User(
            UUID.fromString(INACTIVE_CUSTOMER_UUID),
            "Gosia",
            "Maniecko",
            INACTIVE_CUSTOMER_EMAIL,
            encoder.encode(INACTIVE_CUSTOMER_PASSWORD),
            "222 222 222",
            LocalDate.of(1996, 1, 1),
            BigDecimal.valueOf(10D),
            UserStatus.INACTIVE,
            Set.of(UserRole.CUSTOMER),
            LocalDateTime.of(2020, 1, 1, 1, 1),
            LocalDateTime.of(2020, 1, 1, 1, 1)
        );
    }

    public static User bornBlockedCustomer() {
        return new User(
            UUID.fromString(BLOCKED_CUSTOMER_UUID),
            "Albert",
            "Miracus",
            BLOCKED_CUSTOMER_EMAIL,
            encoder.encode(BLOCKED_CUSTOMER_PASSWORD),
            "000 000 000",
            LocalDate.of(1996, 1, 1),
            BigDecimal.valueOf(30D),
            UserStatus.BLOCKED,
            Set.of(UserRole.CUSTOMER),
            LocalDateTime.of(2020, 1, 1, 1, 1),
            LocalDateTime.of(2020, 1, 1, 1, 1)
        );
    }
}
