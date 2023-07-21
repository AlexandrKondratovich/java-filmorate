package ru.yandex.practicum.filmorate.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {

    }

    public UserNotFoundException(String msg) {
        super(msg);
    }

    public UserNotFoundException(long id) {
        super("Пользователь с ID=" + id + " не найден.");
    }
}
