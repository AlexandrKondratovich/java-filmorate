package ru.yandex.practicum.filmorate.exception;

public class MpaNotFoundException extends RuntimeException {
    public MpaNotFoundException() {
    }

    public MpaNotFoundException(String msg) {
        super(msg);
    }

    public MpaNotFoundException(long id) {
        super("Рейтинг с ID=" + id + " не найден.");
    }
}