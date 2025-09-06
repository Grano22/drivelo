package io.github.grano22.carfleetapp.usermanagement.assembler;

import io.github.grano22.carfleetapp.shared.ViewFormatters;
import io.github.grano22.carfleetapp.usermanagement.contract.UserDetailsView;
import io.github.grano22.carfleetapp.usermanagement.domain.UserRole;
import io.github.grano22.carfleetapp.usermanagement.infrastructure.persistance.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CustomersListingAssembler {
    private final UserRepository userRepository;

    public CustomersListingAssembler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDetailsView[] assemble() {
        return userRepository.findByRoles(Set.of(UserRole.CUSTOMER)).stream()
            .map(user -> new UserDetailsView(
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getBirthDate().toString(),
                    0,
                    user.getStatus().name(),
                    ViewFormatters.formatDateTime(user.getCreatedAt()),
                    ViewFormatters.formatDateTime(user.getUpdatedAt())
            ))
            .toArray(UserDetailsView[]::new)
        ;
    }
}
