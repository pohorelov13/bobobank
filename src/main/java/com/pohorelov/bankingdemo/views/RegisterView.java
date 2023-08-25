package com.pohorelov.bankingdemo.views;

import com.pohorelov.bankingdemo.backend.model.User;
import com.pohorelov.bankingdemo.backend.service.UserService;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;


@Route("reg")
@PageTitle("Registration")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {

    public RegisterView(UserService userService) {
        RegistrationForm registrationForm = new RegistrationForm(Form.REGISTER, new User());
        setHorizontalComponentAlignment(Alignment.CENTER, registrationForm);
        add(registrationForm);
        RegistrationFormBinder registrationFormBinder = new RegistrationFormBinder(userService, registrationForm, new User());
        registrationFormBinder.addBindingAndValidation();
    }

}
