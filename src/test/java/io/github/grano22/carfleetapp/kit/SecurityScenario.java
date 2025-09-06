package io.github.grano22.carfleetapp.kit;

import io.github.grano22.carfleetapp.usermanagement.User;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SecurityScenario {
    public static MockHttpSession alreadyAuthenticated(User user) {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword(), user.getAuthorities());

        SecurityContextImpl securityContext = new SecurityContextImpl(auth);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

        return session;
    }

    public static @NonNull MockHttpSession afterPerformedLogin(String email, String password, MockMvc mockMvc) throws Exception {
        MvcResult loginResult = mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", email)
                .param("password", password)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
            )
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.message").value("Login successful"))
            .andReturn();

        if (loginResult.getRequest().getSession(false) instanceof MockHttpSession session) {
            return session;
        }

        throw new IllegalStateException("Login did not create session");
    }
}
