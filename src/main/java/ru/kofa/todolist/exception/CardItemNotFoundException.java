package ru.kofa.todolist.exception;

public class CardItemNotFoundException extends RuntimeException {
    public CardItemNotFoundException(String message) {
        super(message);
    }
}
