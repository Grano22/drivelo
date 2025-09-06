package io.github.grano22.carfleetapp.usermanagement;

import io.github.grano22.carfleetapp.config.SecurityConfig;
import io.github.grano22.carfleetapp.config.UnitTestRepositories;
import io.github.grano22.carfleetapp.kit.SecurityScenario;
import io.github.grano22.carfleetapp.kit.UsersMother;
import io.github.grano22.carfleetapp.usermanagement.actions.AddCustomerUseCase;
import io.github.grano22.carfleetapp.usermanagement.contract.AddCustomerRequest;
import io.github.grano22.carfleetapp.usermanagement.domain.UserStatus;
import io.github.grano22.carfleetapp.usermanagement.infrastructure.persistance.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserManagementController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import({DbUserProvider.class, SecurityConfig.class, UnitTestRepositories.class})
public class UserManagementControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @MockitoBean
    AddCustomerUseCase addCustomerUseCase;

    Map<String, User> users = new ConcurrentHashMap<>();

    @BeforeAll
    public void setupBeforeAll() {
        users.put(UsersMother.ADMIN_UUID, userRepository.save(UsersMother.bornAdmin()));
        users.put(UsersMother.MANAGER_UUID, userRepository.save(UsersMother.bornManager()));
        users.put(UsersMother.CUSTOMER_UUID, userRepository.save(UsersMother.bornCustomer()));
    }

    @Test
    public void saveCustomer_returns200_whenSuccessful() throws Exception {
        // Arrange
        var session = SecurityScenario.afterPerformedLogin(UsersMother.MANAGER_EMAIL, UsersMother.MANAGER_PASSWORD, mockMvc);
        doNothing().when(addCustomerUseCase).execute(any(AddCustomerRequest.class));

        // Act & Assert
        mockMvc.perform(
             post("/service/user-management/v1/customer/add")
                .content("""
                {
                    "firstName": "Alice",
                    "lastName":"Dritakova",
                    "password": "abcd12345",
                    "email": "alice@example.com",
                    "phone": "123 456 789",
                    "birthDate": "2000-01-01",
                    "address": "Moscow, Lenina street 1",
                    "status": "ACTIVE"
                }
                """)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        )
            .andExpect(status().isOk())
        ;
        ArgumentCaptor<AddCustomerRequest> requestCaptor = ArgumentCaptor.forClass(AddCustomerRequest.class);
        verify(addCustomerUseCase).execute(requestCaptor.capture());

        AddCustomerRequest passedRequest = requestCaptor.getValue();
        assertThat(passedRequest).isEqualTo(
            new AddCustomerRequest(
            "Alice",
            "Dritakova",
            "abcd12345",
                "alice@example.com",
                "123 456 789",
                LocalDate.of(2000, 1, 1),
                "Moscow, Lenina street 1",
                UserStatus.ACTIVE
            )
        );
    }

    @Test
    public void saveCustomer_returns403_whenNoSufficientPermission() throws Exception {
        // Arrange
        var session = SecurityScenario.afterPerformedLogin(UsersMother.CUSTOMER_EMAIL, UsersMother.CUSTOMER_PASSWORD, mockMvc);
        doNothing().when(addCustomerUseCase).execute(any(AddCustomerRequest.class));

        // Act & Assert
        mockMvc.perform(
            post("/service/user-management/v1/customer/add")
            .content("""
            {
                "firstName": "Alice",
                "lastName":"Dritakova",
                "password": "abcd12345",
                "email": "alice@example.com",
                "phone": "123 456 789",
                "birthDate": "2000-01-01",
                "address": "Moscow, Lenina street 1",
                "status": "ACTIVE"
            }
            """)
            .contentType(MediaType.APPLICATION_JSON)
            .session(session)
        )
            .andExpect(status().isForbidden())
        ;
    }
}
