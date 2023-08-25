package com.pohorelov.bankingdemo.views;


import com.pohorelov.bankingdemo.backend.model.User;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
public class RegistrationForm extends FormLayout {

    private H3 title;
    private TextField firstName;
    private TextField lastName;
    private TextField email;
    private PasswordField password;
    private PasswordField passwordConfirm;
    private Span errorMessageField;
    private Button submitButton;
    private Button homeButton;
    private final User user;
    //some differences between register and changing
    private final Form form;


    public RegistrationForm(Form form, User user) {
        this.form = form;
        this.user = user;

        configureForm(form);
    }

    private void configureForm(Form form) {
        firstName = new TextField("First name");
        firstName.setPlaceholder("John");
        lastName = new TextField("Last name");
        lastName.setPlaceholder("Doe");
        email = new TextField("Email");
        email.setPlaceholder("your@email.com");
        password = new PasswordField("Password");
        passwordConfirm = new PasswordField("Confirm password");

        homeButton = new Button("Back to home");
        homeButton.setVisible(false);
        homeButton.addClickListener(event -> UI.getCurrent().getPage().setLocation("/"));
        switch (form) {
            case REGISTER -> setRegisterForm();
            case UPDATE -> setUpdateForm();
        }
        setRequiredIndicatorVisible(firstName, lastName, email, password,
                passwordConfirm);

        errorMessageField = new Span();
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(title, firstName, lastName, email, password,
                passwordConfirm, errorMessageField,
                submitButton, homeButton);

        // Max width of the Form
        setMaxWidth("500px");

        // Allow the form layout to be responsive.
        // On device widths 0-490px we have one column.
        // Otherwise, we have two columns.
        setResponsiveSteps(
                new ResponsiveStep("0", 1, ResponsiveStep.LabelsPosition.TOP),
                new ResponsiveStep("490px", 2, ResponsiveStep.LabelsPosition.TOP));

        // These components always take full width
        setColspan(title, 2);
        setColspan(email, 2);
        setColspan(errorMessageField, 2);
        setColspan(submitButton, 2);
    }

    void setRegisterForm() {
        title = new H3("Signup form");
        submitButton = new Button("REGISTER");
    }

    void setUpdateForm() {
        title = new H3("EDIT CONFIGURATION");
        firstName.setValue(user.getFirstName());
        lastName.setValue(user.getLastName());
        email.setValue(user.getEmail());
        email.addValueChangeListener(event -> Notification.show("You must login after changing email")
                .addThemeVariants(NotificationVariant.LUMO_CONTRAST));
        submitButton = new Button("SAVE");
    }

    public PasswordField getPasswordField() {
        return password;
    }

    public TextField getFirstNameField() {
        return firstName;
    }

    public TextField getEmailField() {
        return email;
    }

    public TextField getLastNameField() {
        return lastName;
    }

    public String getFirstName() {
        return firstName.getValue();
    }

    public String getLastName() {
        return lastName.getValue();
    }

    public String getEmail() {
        return email.getValue();
    }

    public String getPassword() {
        return password.getValue();
    }

    public PasswordField getPasswordConfirmField() {
        return passwordConfirm;
    }

    private void setRequiredIndicatorVisible(HasValueAndElement<?, ?>... components) {
        Stream.of(components).forEach(comp -> comp.setRequiredIndicatorVisible(true));
    }

}