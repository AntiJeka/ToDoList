package ru.kofa.todolist.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CardItemDto {
    private Long id;
    private String name;
    private String comment;
    private String priority;
    private boolean completed;
    private String createdAt;
}
