package ru.yandex.practicum.filmorate.exception;

public class ReviewNotExistObject extends RuntimeException {
    public ReviewNotExistObject(String msg) {
        super(msg);
    }

    public ReviewNotExistObject(long id) {
        super("Отзыв с ID=" + id + " не найден.");
    }
}
