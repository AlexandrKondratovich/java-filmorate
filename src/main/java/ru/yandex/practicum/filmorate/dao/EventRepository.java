package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository {

    Event getById(long eventId);

    List<Event> getEventsByUserId(long userId);

    void deleteByUserId(long userId);

    Event add(Event event);

    Event update(Event event);

    void delete(long eventId);

    static Event createEvent(long userId, EventType eventType, long entityId, Operation operation) {
        Event event = new Event();
        event.setUserId(userId);
        event.setEventType(eventType);
        event.setEntityId(entityId);
        event.setOperation(operation);
        event.setTimestamp(Timestamp.valueOf(LocalDateTime.now()).getTime());
        return event;
    }
}
