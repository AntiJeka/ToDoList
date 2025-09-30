package ru.kofa.todolist.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kofa.todolist.enums.Role;
import ru.kofa.todolist.exception.UserNotFoundException;
import ru.kofa.todolist.model.Card;
import ru.kofa.todolist.model.User;
import ru.kofa.todolist.repository.UserRepository;
import ru.kofa.todolist.request.RegistrationRequest;
import ru.kofa.todolist.request.UserUpdateRequest;
import ru.kofa.todolist.service.card.CardService;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CardService cardService;

    @Override
    @Transactional
    public User createUser(RegistrationRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.getRoles().add(Role.ROLE_USER);

        Card card = new Card();
        card = cardService.createCard(card);
        user.setCard(card);

        return userRepository.save(user);
    }

    @Override
    public User getUserByUsername(String name) {
        return userRepository.findUserByUsername(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public User updateUser(UserUpdateRequest request, Long id) {
        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setUsername(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());

        return userRepository.save(user);
    }

    @Override
    public void deleteUserById(Long id) {
        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        userRepository.delete(user);
    }

    @Override
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email);
    }
}
