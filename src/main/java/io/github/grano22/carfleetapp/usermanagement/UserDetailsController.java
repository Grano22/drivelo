package io.github.grano22.carfleetapp.usermanagement;

import io.github.grano22.carfleetapp.usermanagement.assembler.CustomerDetailsAssembler;
import io.github.grano22.carfleetapp.usermanagement.assembler.CustomersListingAssembler;
import io.github.grano22.carfleetapp.usermanagement.contract.AccountOwnerDetailsResponse;
import io.github.grano22.carfleetapp.usermanagement.contract.UserDetailsView;
import io.github.grano22.carfleetapp.usermanagement.infrastructure.persistance.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/service/user/v1")
public class UserDetailsController {
    private final UserRepository userRepository;
    private final CustomersListingAssembler customersListingAssembler;
    private final CustomerDetailsAssembler customerDetailsAssembler;

    public UserDetailsController(
            UserRepository userRepository,
            CustomersListingAssembler customersListingAssembler, CustomerDetailsAssembler customerDetailsAssembler
    ) {
        this.userRepository = userRepository;
        this.customersListingAssembler = customersListingAssembler;
        this.customerDetailsAssembler = customerDetailsAssembler;
    }

    @GetMapping
    public AccountOwnerDetailsResponse getCurrentDetails(@AuthenticationPrincipal User user) {
        var freshUserData = userRepository.findById(user.getId())
            .orElseThrow(() -> new RuntimeException("User not found"))
        ;

        DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

        return new AccountOwnerDetailsResponse(
            freshUserData.getFirstName(),
            freshUserData.getLastName(),
            freshUserData.getEmail(),
            freshUserData.getPhone(),
            freshUserData.getBirthDate().toString(),
            freshUserData.getCredits().doubleValue(),
            freshUserData.getStatus().name(),
            freshUserData.getRoles().stream()
                .map(Enum::name)
                .sorted()
                .toArray(String[]::new),
            freshUserData.getRoles().stream()
                 .flatMap(role -> role.getPermissions().stream())
                 .map(Enum::name)
                 .sorted()
                 .toArray(String[]::new),
            freshUserData.getCreatedAt().atOffset(ZoneOffset.UTC).format(isoFormatter),
            freshUserData.getUpdatedAt().atOffset(ZoneOffset.UTC).format(isoFormatter)
        );
    }

    @GetMapping("/customer/{userId}")
    @PreAuthorize("hasAuthority(T(io.github.grano22.carfleetapp.usermanagement.domain.UserPermission).VIEW_CUSTOMERS.name())")
    public UserDetailsView getCustomerDetails(@PathVariable("userId") UUID userId) {
        // TODO: Add dedicated permission

        return customerDetailsAssembler.assemble(userId);
    }

    @GetMapping("/customers")
    @PreAuthorize("hasAuthority(T(io.github.grano22.carfleetapp.usermanagement.domain.UserPermission).VIEW_CUSTOMERS.name())")
    public UserDetailsView[] getCustomers() {
        // TODO: Add pagination and filtering

        return customersListingAssembler.assemble();
    }
}
