package ru.kofa.todolist.request;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String name;
    private String password;
    private String email;
}
