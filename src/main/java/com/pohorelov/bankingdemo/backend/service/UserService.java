package com.pohorelov.bankingdemo.backend.service;

import com.pohorelov.bankingdemo.backend.model.User;
import com.pohorelov.bankingdemo.backend.repo.UserRepo;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepo repo;
    private final AuthService authService;

    public void register(User user) {
        //check if email already exist
        repo.findUserByEmail(user.getUsername()).ifPresentOrElse((u) -> {
            if (UI.getCurrent() != null) Notification.show("User with email already exist");
        }, () -> {
            //if not, save user to DB and auto login with authManager
            String password = user.getPassword();
            user.setPassword(passwordEncoder.encode(password));
            repo.save(user);
            authService.authWithAuthManager(user.getUsername(), password);
            if (UI.getCurrent() != null) UI.getCurrent().getPage().setLocation("/");
        });
    }

    //change user info
    public void changeData(User user, String firstName, String lastName, String email, String password) {
        //check if user change email
        if (!user.getEmail().equals(email)) {
            //if email was changed, check if new email exist in DB
            if (checkEmailExist(email)) {
                if (UI.getCurrent() != null) Notification.show("User with email already exist");
                return;
            }
            if (UI.getCurrent() != null) Notification.show("You must login after changing email")
                    .addThemeVariants(NotificationVariant.LUMO_CONTRAST);
            user.setEmail(email);
        }

        if (!(password.isEmpty() || password.isBlank())) {
            user.setPassword(passwordEncoder.encode(password));
        }
        user.setFirstName(firstName);
        user.setLastName(lastName);
        repo.save(user);
        if (UI.getCurrent() != null) Notification.show("Saved");
    }

    private boolean checkEmailExist(String email) {
        return repo.findUserByEmail(email).isPresent();
    }
}
