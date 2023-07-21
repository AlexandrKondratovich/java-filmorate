package ru.yandex.practicum.filmorate.exception;

public class GenreNotFoundException extends RuntimeException {
    public GenreNotFoundException() {

    }

    public GenreNotFoundException(String msg) {
        super(msg);
    }

    public GenreNotFoundException(long id) {
        super("Жанр с ID=" + id + " не найден.");
    }
}
