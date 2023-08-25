package com.pohorelov.bankingdemo.backend.service;

import com.pohorelov.bankingdemo.backend.model.User;
import com.pohorelov.bankingdemo.backend.repo.UserRepo;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service

public class AuthService {
    private final UserRepo userRepo;
    private static final String LOGOUT_SUCCESS_URL = "/login";

    private final HttpServletRequest request;
    private final SecurityContextRepository securityContextRepository;
    private final HttpServletResponse response;
    private final AuthenticationManager authenticationManager;


    //try to get user from security context, if user log in
    public Optional<UserDetails> getAuthenticatedUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Object principal = context.getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return Optional.ofNullable((UserDetails) context.getAuthentication().getPrincipal());
        }
        // Anonymous or no authentication.
        return Optional.empty();
    }

    //add user to context, use this, when user register and doesn't need login after this
    public void authWithAuthManager(String username, String password) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
        authToken.setDetails(new WebAuthenticationDetails(request));
        Authentication authentication = authenticationManager.authenticate(authToken);
        SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
                .getContextHolderStrategy();
        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }

    //get user from db by userDetails
    public Optional<User> getUser() {
        UserDetails userDetails = getAuthenticatedUser().orElse(new User());
        return userRepo.findUserByEmail(userDetails.getUsername());
    }

    public void logout() {
        UI.getCurrent().getPage().setLocation(LOGOUT_SUCCESS_URL);
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(
                VaadinServletRequest.getCurrent().getHttpServletRequest(), null,
                null);
    }
}
