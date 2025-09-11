package io.github.grano22.carfleetapp.usermanagement;

import io.github.grano22.carfleetapp.shared.validation.OnCreate;
import io.github.grano22.carfleetapp.usermanagement.actions.AddCustomerUseCase;
import io.github.grano22.carfleetapp.usermanagement.actions.UpdateCustomerUseCase;
import io.github.grano22.carfleetapp.usermanagement.contract.SaveCustomerRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/service/user-management/v1")
public class UserManagementController {
    private final AddCustomerUseCase addCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;

    public UserManagementController(
        AddCustomerUseCase addCustomerUseCase,
        UpdateCustomerUseCase updateCustomerUseCase
    ) {
        this.addCustomerUseCase = addCustomerUseCase;
        this.updateCustomerUseCase = updateCustomerUseCase;
    }

    @PutMapping("/customer/edit/{userId}")
    @PreAuthorize("hasAuthority(T(io.github.grano22.carfleetapp.usermanagement.domain.UserPermission).MODIFY_CUSTOMERS.name())")
    public void updateCustomer(@PathVariable("userId") UUID userId, @RequestBody @Valid SaveCustomerRequest request) {
        updateCustomerUseCase.execute(userId, request);
    }

    @RequestMapping("/customer/add")
    @PreAuthorize("hasAuthority(T(io.github.grano22.carfleetapp.usermanagement.domain.UserPermission).ADD_CUSTOMERS.name())")
    public void addCustomer(@RequestBody @Validated(OnCreate.class) SaveCustomerRequest request) {
        addCustomerUseCase.execute(request);
    }
}
