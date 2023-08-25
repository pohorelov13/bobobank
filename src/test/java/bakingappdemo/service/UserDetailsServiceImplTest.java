package bakingappdemo.service;

import com.pohorelov.bankingdemo.backend.model.User;
import com.pohorelov.bankingdemo.backend.repo.UserRepo;
import com.pohorelov.bankingdemo.backend.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.assertj.core.api.ClassBasedNavigableIterableAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;


class UserDetailsServiceImplTest {
    @Mock
    private UserRepo userRepo;

    private UserDetailsServiceImpl userDetailsService;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = openMocks(this);
        userDetailsService = new UserDetailsServiceImpl(userRepo);

    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void loadUserByUsername() {
        String excepted = "test@gmail.com";
        User user = new User(excepted, null, null, "testpassword", null);
        when(userRepo.findUserByEmail(excepted)).thenReturn(Optional.of(user));
        UserDetails exceptedUser = userDetailsService.loadUserByUsername(excepted);
        assertEquals(excepted, exceptedUser.getUsername());
    }
}