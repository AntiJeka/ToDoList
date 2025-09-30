package ru.kofa.todolist.service.user;

import ru.kofa.todolist.model.User;
import ru.kofa.todolist.request.LoginRequest;
import ru.kofa.todolist.request.RegistrationRequest;
import ru.kofa.todolist.request.UserUpdateRequest;

public interface IUserService {
    User createUser(RegistrationRequest request);
    User getUserByUsername(String name);
    User updateUser(UserUpdateRequest request, Long id);
    void deleteUserById(Long id);

    User getAuthenticatedUser();
}
