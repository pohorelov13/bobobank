package bakingappdemo.service;

import com.pohorelov.bankingdemo.backend.model.Role;
import com.pohorelov.bankingdemo.backend.model.User;
import com.pohorelov.bankingdemo.backend.repo.UserRepo;
import com.pohorelov.bankingdemo.backend.service.AuthService;
import com.pohorelov.bankingdemo.backend.service.UserService;
import com.vaadin.flow.component.UI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private UserRepo repo;
    @Mock
    private AuthService authService;

    //@InjectMocks
    private UserService underTest;
    private AutoCloseable autoCloseable;
    private User user;

    @BeforeEach
    void setUp() {
        autoCloseable = openMocks(this);
        user = new User("test@gmail.com", "test", "test", "test", Role.USER);
        underTest = new UserService(passwordEncoder, repo, authService);

    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void registerWhenEmailDoesNotExist() {

        String encodePassword = "encodePassword";
        String password = "test";

        when(repo.findUserByEmail(user.getEmail())).thenReturn(Optional.empty());
        doNothing().when(authService).authWithAuthManager(anyString(), anyString());

        when(passwordEncoder.encode(user.getPassword())).thenReturn(encodePassword);

        ArgumentCaptor<User> userArg = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<String> emailArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> passArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> encPass = ArgumentCaptor.forClass(String.class);

        underTest.register(user);

        verify(passwordEncoder).encode(encPass.capture());
        verify(repo).save(userArg.capture());
        verify(authService).authWithAuthManager(emailArg.capture(), passArg.capture());

        assertEquals(user, userArg.getValue());
        assertEquals(user.getEmail(), emailArg.getValue());
        assertEquals(password, passArg.getValue());
        assertEquals(user.getPassword(), encodePassword);

    }

    @Test
    void shouldNotCallMethodRegisterWhenEmailExists() {
        when(repo.findUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        underTest.register(user);

        verify(passwordEncoder, times(0)).encode(anyString());
        verify(repo, times(0)).save(any());
        verify(authService, times(0)).authWithAuthManager(anyString(), anyString());

    }

    @Test
    void changeDataWithoutChangingEmail() {

        String newFirstName = "newFirst";
        String newLastName = "newLast";
        String newPassword = "newFirst";
        String encodePassword = "encodeNewPassword";

        User newUser = new User("test@gmail.com", newFirstName, newLastName, encodePassword, Role.USER);


        when(passwordEncoder.encode(newPassword)).thenReturn(encodePassword);

        ArgumentCaptor<User> userArg = ArgumentCaptor.forClass(User.class);

        underTest.changeData(user, newFirstName, newLastName, user.getEmail(), newPassword);

        verify(repo).save(userArg.capture());


        assertEquals(newUser, userArg.getValue());
    }

    @Test
    void changeDataWithChangingEmailWhenAlreadyExist() {

        String newFirstName = "newFirst";
        String newLastName = "newLast";
        String newPassword = "newFirst";
        String newEmail = "newTest@gmail.com";

        when(repo.findUserByEmail(anyString())).thenReturn(Optional.of(new User()));
        underTest.changeData(user, newFirstName, newLastName, newEmail, newPassword);

        verify(repo, times(0)).save(any());
        verify(passwordEncoder, times(0)).encode(any());

    }

    @Test
    void changeDataWithChangingEmailWhenDoesNotExist() {

        String newFirstName = "newFirst";
        String newLastName = "newLast";
        String newPassword = "newFirst";
        String newEmail = "newTest@gmail.com";
        String encodePassword = "encodeNewPassword";

        User newUser = new User(newEmail, newFirstName, newLastName, encodePassword, Role.USER);


        when(passwordEncoder.encode(newPassword)).thenReturn(encodePassword);
        when(repo.findUserByEmail(newEmail)).thenReturn(Optional.empty());

        ArgumentCaptor<User> userArg = ArgumentCaptor.forClass(User.class);

        underTest.changeData(user, newFirstName, newLastName, newEmail, newPassword);

        verify(repo).save(userArg.capture());

        assertEquals(newUser, userArg.getValue());
    }
}