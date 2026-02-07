package ru.practicum.ewm.service.compilation.exception;

public class TitleAlreadyExistsException extends RuntimeException {
    public TitleAlreadyExistsException(String title) {
        super("Compilation with title '" + title + "' already exists");
    }
}