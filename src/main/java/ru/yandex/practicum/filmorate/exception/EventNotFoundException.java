package ru.yandex.practicum.filmorate.exception;

public class EventNotFoundException extends RuntimeException {

    public EventNotFoundException(String msg) {
        super((msg));
    }
    public EventNotFoundException(long id) {
        super("Событие с ID=" + id + " не найдено.");
    }
}
