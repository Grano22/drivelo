package io.github.grano22.carfleetapp.usermanagement.actions;

import io.github.grano22.carfleetapp.kit.AdjustableClock;
import io.github.grano22.carfleetapp.usermanagement.contract.SaveCustomerRequest;
import io.github.grano22.carfleetapp.usermanagement.domain.UserRole;
import io.github.grano22.carfleetapp.usermanagement.domain.UserStatus;
import io.github.grano22.carfleetapp.usermanagement.infrastructure.persistance.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AddCustomerUseCaseTest {
    @Autowired
    private AddCustomerUseCase addCustomerUseCase;

    @Autowired
    private AdjustableClock clock;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    public void testNewCustomerCanBeSuccessfullyAdded() {
        // Assert
        clock.set(LocalDateTime.of(2020, 1, 1, 0, 0));
        SaveCustomerRequest request = new SaveCustomerRequest(
            "Anton",
            "Michaluk",
            "abcd1234",
            "anton@michaluk.org",
            "123 123 123",
            LocalDate.of(2003, 12, 11),
            UserStatus.ACTIVE,
            BigDecimal.valueOf(100D)
        );

        // Act
        addCustomerUseCase.execute(request);

        // Assert
        assertThat(userRepository.findAll())
            .hasSize(1)
            .anySatisfy(user -> {
                assertThat(user.getFirstName()).isEqualTo("Anton");
                assertThat(user.getLastName()).isEqualTo("Michaluk");
                assertThat(user.getEmail()).isEqualTo("anton@michaluk.org");
                assertThat(user.getPhone()).isEqualTo("123 123 123");
                assertThat(user.getBirthDate()).isEqualTo(LocalDate.of(2003, 12, 11));
                assertThat(user.getRoles()).isEqualTo(Set.of(UserRole.CUSTOMER));
                assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
                assertThat(user.getCreatedAt()).isEqualTo(LocalDateTime.of(2020, 1, 1, 0, 0));
            })
        ;
    }
}
