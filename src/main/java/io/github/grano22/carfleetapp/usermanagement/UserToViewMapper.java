package io.github.grano22.carfleetapp.usermanagement;

import io.github.grano22.carfleetapp.shared.ViewFormatters;
import io.github.grano22.carfleetapp.usermanagement.contract.UserDetailsView;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

public class UserToViewMapper {
    public static @NotNull UserDetailsView map(@NonNull User user) {
        return new UserDetailsView(
            user.getId().toString(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getPhone(),
            user.getBirthDate().toString(),
            user.getCredits().doubleValue(),
            user.getStatus().name(),
            ViewFormatters.formatDateTime(user.getCreatedAt()),
            ViewFormatters.formatDateTime(user.getUpdatedAt())
        );
    }
}
