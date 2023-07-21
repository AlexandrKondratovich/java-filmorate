package ru.yandex.practicum.filmorate.exception;

public class FilmNotFoundException extends RuntimeException {
    public FilmNotFoundException() {

    }

    public FilmNotFoundException(String msg) {
        super(msg);
    }

    public FilmNotFoundException(long id) {
        super("Фильм с ID=" + id + " не найден.");
    }
}