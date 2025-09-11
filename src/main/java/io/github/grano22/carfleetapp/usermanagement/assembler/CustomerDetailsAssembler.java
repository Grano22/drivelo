package io.github.grano22.carfleetapp.usermanagement.assembler;

import io.github.grano22.carfleetapp.usermanagement.UserToViewMapper;
import io.github.grano22.carfleetapp.usermanagement.contract.UserDetailsView;
import io.github.grano22.carfleetapp.usermanagement.domain.MissingRequestedUser;
import io.github.grano22.carfleetapp.usermanagement.infrastructure.persistance.UserRepository;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomerDetailsAssembler {
    private final UserRepository userRepository;

    public CustomerDetailsAssembler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public @NonNull UserDetailsView assemble(@NonNull UUID userId) {
        return userRepository
            .findById(userId).map(UserToViewMapper::map)
            .orElseThrow(() -> new MissingRequestedUser(userId))
        ;
    }
}
