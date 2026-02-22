package ru.kofa.todolist.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kofa.todolist.exception.UserNotFoundException;
import ru.kofa.todolist.model.Card;
import ru.kofa.todolist.model.User;
import ru.kofa.todolist.repository.UserRepository;
import ru.kofa.todolist.request.RegistrationRequest;
import ru.kofa.todolist.request.UserUpdateRequest;
import ru.kofa.todolist.service.card.CardService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CardService cardService;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_Success() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("Test1");
        request.setPassword("1234");
        request.setConfirmPassword("1234");
        request.setEmail("test1@gmail.com");

        User savedUser = getNewUser();
        Card card = savedUser.getCard();

        when(passwordEncoder.encode("1234")).thenReturn("encodePassword");
        when(cardService.createCard(any(Card.class))).thenReturn(card);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.createUser(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCard().getId()).isEqualTo(10L);
        assertThat(result.getUsername()).isEqualTo("Test1");
        assertThat(result.getEmail()).isEqualTo("test1@gmail.com");

        verify(passwordEncoder).encode("1234");
        verify(cardService).createCard(any(Card.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_Success() {
        User user = getNewUser();

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setName("user");
        updateRequest.setPassword("1234");
        updateRequest.setEmail("test1@gmail.com");

        when(userRepository.findUserById(1L))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode("1234"))
                .thenReturn("encodePassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User updateUser = userService.updateUser(updateRequest, 1L);

        assertThat(updateUser).isNotNull();
        assertThat(updateUser.getUsername()).isEqualTo("user");

        verify(userRepository).findUserById(1L);
        verify(passwordEncoder).encode("1234");
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_UserNotFound_ThrowsUserNotFoundExc() {
        when(userRepository.findUserById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(new UserUpdateRequest(), 99L));
    }

    @Test
    void deleteUser_Success() {
        User user = getNewUser();

        when(userRepository.findUserById(user.getId()))
                .thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        userService.deleteUserById(user.getId());

        verify(userRepository).findUserById(user.getId());
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_UserNotFound_ThrowsUserNotFoundExc() {
        when(userRepository.findUserById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.deleteUserById(99L));
        verify(userRepository, never()).delete(any());
    }

    @Test
    void authenticatedUser_Success() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn("test1@gmail.com");

        User user = getNewUser();
        when(userRepository.findByEmail("test1@gmail.com")).thenReturn(user);

        User result = userService.getAuthenticatedUser();

        assertThat(result.getEmail()).isEqualTo("test1@gmail.com");
    }

    private User getNewUser() {
        Card card = new Card();
        card.setId(10L);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setEmail("test1@gmail.com");
        savedUser.setPassword("1234");
        savedUser.setCard(card);

        return savedUser;
    }
}
