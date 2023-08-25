package com.pohorelov.bankingdemo.views;

import com.pohorelov.bankingdemo.backend.model.User;
import com.pohorelov.bankingdemo.backend.service.AuthService;
import com.pohorelov.bankingdemo.backend.service.UserService;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route("settings")
@PermitAll
public class SettingsView extends VerticalLayout {

    public SettingsView(AuthService authService, UserService userService) {


        User user = authService.getUser().orElse(new User());
        RegistrationForm registrationForm = new RegistrationForm(Form.UPDATE, user);
        // Center the RegistrationForm
        setHorizontalComponentAlignment(Alignment.CENTER, registrationForm);

        add(registrationForm);
        RegistrationFormBinder registrationFormBinder = new RegistrationFormBinder(userService, registrationForm, user);
        registrationFormBinder.addBindingAndValidation();
    }
}