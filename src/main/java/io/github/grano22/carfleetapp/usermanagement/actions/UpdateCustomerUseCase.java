package io.github.grano22.carfleetapp.usermanagement.actions;

import io.github.grano22.carfleetapp.shared.BuilderPatcher;
import io.github.grano22.carfleetapp.usermanagement.User;
import io.github.grano22.carfleetapp.usermanagement.contract.SaveCustomerRequest;
import io.github.grano22.carfleetapp.usermanagement.domain.MissingRequestedUser;
import io.github.grano22.carfleetapp.usermanagement.infrastructure.persistance.UserRepository;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UpdateCustomerUseCase {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    public UpdateCustomerUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder, Clock clock) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
    }

    public void execute(UUID userId, SaveCustomerRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new MissingRequestedUser(userId));
        User.UserBuilder builder = user.toBuilder();

        List<String> changedFields = new ArrayList<>();
        builder = BuilderPatcher.patch(request, builder, User.UserBuilder.class, changedFields::add);

        if (changedFields.contains("password")) {
            builder = builder.password(passwordEncoder.encode(request.password()));
        }

        if (!changedFields.isEmpty()) {
            builder = builder.updatedAt(LocalDateTime.now(clock));
        }

        var updatedUser = builder.build();

        logger.info("Updating User {} updated with fields: {}", userId, Strings.join(changedFields, ','));

        userRepository.save(updatedUser);
    }
}
