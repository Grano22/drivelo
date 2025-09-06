package io.github.grano22.carfleetapp.usermanagement;

import io.github.grano22.carfleetapp.usermanagement.actions.AddCustomerUseCase;
import io.github.grano22.carfleetapp.usermanagement.contract.AddCustomerRequest;
import io.github.grano22.carfleetapp.usermanagement.domain.UserPermission;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service/user-management/v1")
public class UserManagementController {
    private final AddCustomerUseCase addCustomerUseCase;

    public UserManagementController(
        AddCustomerUseCase addCustomerUseCase
    ) {
        this.addCustomerUseCase = addCustomerUseCase;
    }

    @RequestMapping("/customer/add")
    @PreAuthorize("hasAuthority(T(io.github.grano22.carfleetapp.usermanagement.domain.UserPermission).ADD_CUSTOMERS.name())")
    public void addCustomer(@RequestBody AddCustomerRequest request) {
        addCustomerUseCase.execute(request);
    }
}
