package com.pohorelov.bankingdemo.views;

import com.pohorelov.bankingdemo.backend.model.User;
import com.pohorelov.bankingdemo.backend.service.UserService;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class RegistrationFormBinder {
    private final UserService userService;
    private final RegistrationForm registrationForm;

    private final static String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
    private final static String NAME_PATTERN = "^[A-Za-zА-Яа-я]{2,20}$";
    private final static String EMAIL_PATTERN = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    ;
    private final static String PASSWORD_RESTRICTION = "Password must contain 8-20 (at least one number, one uppercase letter, one lowercase letter and a special character)";
    private final static String NAME_RESTRICTION = "Only Latin and Cyrillic characters (from 2 to 20 characters)";
    private final static String EMAIL_RESTRICTION = "Enter correct email";
    private final static String PASSWORDS_DONT_MATCH = "Passwords dont match";
    private final static Pattern passwordPattern = Pattern.compile(PASSWORD_PATTERN);
    private final static Pattern namePattern = Pattern.compile(NAME_PATTERN);
    private final static Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);

    /**
     * Flag for disabling first run for password validation
     */
    private boolean enablePasswordValidation;
    private boolean enableFirstNameValidation;
    private final User userBean;
    private final Form type;

    public RegistrationFormBinder(UserService userService, RegistrationForm registrationForm, User userBean) {
        this.userService = userService;
        this.registrationForm = registrationForm;
        this.userBean = userBean;
        this.type = registrationForm.getForm();
    }

    /**
     * Method to add the data binding and validation logics
     * to the registration form
     */
    public void addBindingAndValidation() {
        BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);
        binder.bindInstanceFields(registrationForm);

        // A custom validator for password fields
        binder.forField(registrationForm.getPasswordField())
                .withValidator(this::passwordValidator).bind("password");

        binder.forField(registrationForm.getFirstNameField()).withValidator(this::nameValidator)
                .bind("firstName");

        binder.forField(registrationForm.getLastNameField()).withValidator(this::nameValidator)
                .bind("lastName");
        binder.forField(registrationForm.getEmailField()).withValidator(this::emailValidator)
                .bind("email");
        // The second password field is not connected to the Binder, but we
        // want the binder to re-check the password validator when the field
        // value changes. The easiest way is just to do that manually.
        registrationForm.getPasswordConfirmField().addValueChangeListener(e -> {
            // The user has modified the second field, now we can validate and show errors.
            // See passwordValidator() for how this flag is used.
            enablePasswordValidation = true;

            binder.validate();
        });

        // Set the label where bean-level error messages go
        binder.setStatusLabel(registrationForm.getErrorMessageField());

        // And finally the submit button
        registrationForm.getSubmitButton().addClickListener(event -> {
            try {
                // Create empty bean to store the details into
                String firstName = registrationForm.getFirstName();
                String lastName = registrationForm.getLastName();
                String email = registrationForm.getEmail();
                String password = registrationForm.getPassword();
                // Run validators and write the values to the bean
                //binder.writeBean(userBean);
                // Typically, you would here call backend to store the bean
                if (type.equals(Form.REGISTER)) {
                    userBean.setFirstName(firstName);
                    userBean.setLastName(lastName);
                    userBean.setEmail(email);
                    userBean.setPassword(password);
                    binder.writeBean(userBean);
                    userService.register(userBean);
                } else {
                    registrationForm.getHomeButton().setVisible(true);
                    userService.changeData(userBean, firstName, lastName, email, password);
                }
                // Show success message if everything went well
            } catch (ValidationException exception) {

            }
        });
    }


    private ValidationResult passwordValidator(String pass1, ValueContext ctx) {


        if (type.equals(Form.UPDATE) && !enablePasswordValidation) {
            // user hasn't visited the field yet, so don't validate just yet, but next time.
            enablePasswordValidation = true;
            return ValidationResult.ok();
        }
        if (pass1 == null || isNoValid(pass1, passwordPattern)) {
            return ValidationResult.error(PASSWORD_RESTRICTION);
        }

        if (!enablePasswordValidation) {
            // user hasn't visited the field yet, so don't validate just yet, but next time.
            enablePasswordValidation = true;
            return ValidationResult.ok();
        }

        String pass2 = registrationForm.getPasswordConfirmField().getValue();

        if (pass1 != null && pass1.equals(pass2)) {
            return ValidationResult.ok();
        }

        return ValidationResult.error(PASSWORDS_DONT_MATCH);
    }

    private ValidationResult nameValidator(String name, ValueContext context) {

        if (name == null || isNoValid(name, namePattern)) {
            return ValidationResult.error(NAME_RESTRICTION);
        }

        if (!enableFirstNameValidation) {
            // user hasn't visited the field yet, so don't validate just yet, but next time.
            enableFirstNameValidation = true;
            return ValidationResult.ok();
        }

        return ValidationResult.ok();
    }

    private ValidationResult emailValidator(String email, ValueContext context) {

        if (email == null || isNoValid(email, emailPattern)) {
            return ValidationResult.error(EMAIL_RESTRICTION);
        }

        return ValidationResult.ok();
    }

    private boolean isNoValid(String test, Pattern passwordPattern) {
        Matcher matcher = passwordPattern.matcher(test);
        return !matcher.matches();
    }
}