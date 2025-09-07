package io.github.grano22.carfleetapp.usermanagement;

import io.github.grano22.carfleetapp.kit.UsersMother;
import io.github.grano22.carfleetapp.usermanagement.infrastructure.persistance.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    public void setupBeforeAll() {
        userRepository.save(UsersMother.bornAdmin());
        userRepository.save(UsersMother.bornManager());
        userRepository.save(UsersMother.bornCustomer());
    }

    @Test
    public void login_returns200_andHasAccessToTheAuthenticatedEndpoint_whenSuccessful() throws Exception {
        // Act & Assert
        MvcResult loginResult = mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", UsersMother.ADMIN_EMAIL)
                .param("password", UsersMother.ADMIN_PASSWORD)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.message").value("Login successful"))
            .andReturn();

        String sessionCookie = loginResult.getResponse().getHeader("Set-Cookie");
        assert sessionCookie != null && sessionCookie.contains("JSESSIONID");
        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);

        Assertions.assertNotNull(session);
        MvcResult currentUserDetails = mockMvc.perform(get("/service/user/v1").session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
        ;

        assertEquals(
            """
            {"firstName":"Admin","lastName":"Root","email":"admin@drivelo.org","phone":"123 123 123","birthDate":"2000-01-01","credits":100.0,"status":"ACTIVE","roles":["ADMIN"],"permissions":["ADD_CAR_RENTAL_OFFERS","ADD_CUSTOMERS","MODIFY_CAR_RENTAL_OFFERS","MODIFY_CUSTOMERS","RENT_CARS","RETURN_CAR","VIEW_AVAILABLE_CAR_OFFERS","VIEW_CAR_RENTAL_OFFERS","VIEW_CUSTOMERS","VIEW_RENTED_CARS"],"createdAt":"2023-01-01T01:01:00.000Z","updatedAt":"2023-01-01T01:01:00.000Z"}""",
            currentUserDetails.getResponse().getContentAsString()
        );
    }
}
