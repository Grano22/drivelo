package io.github.grano22.carfleetapp.usermanagement;

import io.github.grano22.carfleetapp.usermanagement.contract.UserDetailsResponse;
import io.github.grano22.carfleetapp.usermanagement.infrastructure.persistance.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/service/user/v1")
public class UserDetailsController {
    private final UserRepository userRepository;

    public UserDetailsController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public UserDetailsResponse getCurrentDetails(@AuthenticationPrincipal User user) {
        var freshUserData = userRepository.findById(user.getId())
            .orElseThrow(() -> new RuntimeException("User not found"))
        ;

        return new UserDetailsResponse(
            freshUserData.getFirstName(),
            freshUserData.getLastName(),
            freshUserData.getEmail(),
            freshUserData.getPhone(),
            freshUserData.getBirthDate().toString(),
            freshUserData.getAddress(),
            freshUserData.getStatus().name(),
            freshUserData.getRoles().stream().map(Enum::name).toArray(String[]::new)
        );
    }
}
