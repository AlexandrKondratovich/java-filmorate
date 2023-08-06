package ru.yandex.practicum.filmorate.exception;

public class LikeAlreadyExistException extends RuntimeException {
    public LikeAlreadyExistException(String msg) {
        super(msg);
    }

    public LikeAlreadyExistException(long userId) {
        super("Юзер с айди " + userId + " Уже ставил отметку данному фильму");
    }
}
