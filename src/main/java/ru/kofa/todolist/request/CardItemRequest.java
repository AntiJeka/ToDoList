package ru.kofa.todolist.request;

import lombok.Data;

@Data
public class CardItemRequest {
    private String name;
    private String comment;
    private String priority;
}
