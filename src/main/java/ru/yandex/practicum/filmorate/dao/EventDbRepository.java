package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.rowMapper.EventRowMapper;
import ru.yandex.practicum.filmorate.exception.EventNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class EventDbRepository implements EventRepository {

    private final NamedParameterJdbcOperations jdbcOperations;

    @Override
    public Event getById(long eventId) {
        checkEventId(eventId);
        final String sqlQuery = "select * " +
                "from EVENTS " +
                "where EVENT_ID = :eventId";
        return jdbcOperations.queryForObject(sqlQuery, Map.of("eventId", eventId), new EventRowMapper());
    }

    @Override
    public List<Event> getEventsByUserId(long userId) {
        final String sqlQuery = "select * " +
                "from EVENTS " +
                "where USER_ID = :userId ";
        return jdbcOperations.query(sqlQuery, Map.of("userId", userId), new EventRowMapper());
    }

    @Override
    public void deleteByUserId(long userId) {
        final String sqlQuery = "delete from EVENTS " +
                "where USER_ID = :userId";
        jdbcOperations.update(sqlQuery, Map.of("userId", userId));
    }

    @Override
    public Event add(Event event) {
        final String sqlQuery = "insert into EVENTS(USER_ID, EVENT_TYPE, ENTITY_ID, OPERATION, TIMESTAMP) " +
                "values (:userId, :eventType, :entityId, :operation, :timestamp) ";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("userId", event.getUserId());
        map.addValue("eventType", event.getEventType().toString());
        map.addValue("entityId", event.getEntityId());
        map.addValue("operation", event.getOperation().toString());
        map.addValue("timestamp", event.getTimestamp());
        jdbcOperations.update(sqlQuery, map, keyHolder);
        event.setEventId(keyHolder.getKey().longValue());
        return getById(event.getEventId());
    }

    @Override
    public Event update(Event event) {
        checkEventId(event.getEventId());
        final String sqlQuery = "update EVENTS " +
                "set USER_ID = :userID, " +
                    "EVENT_TYPE = :eventType, " +
                    "ENTITY_ID = :entityId, " +
                    "OPERATION = :operation, " +
                    "TIMESTAMP = :timestamp " +
                "where EVENT_ID = :eventId";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("eventId", event.getEventId());
        map.addValue("userId", event.getUserId());
        map.addValue("eventType", event.getEventType());
        map.addValue("entityId", event.getEntityId());
        map.addValue("operation", event.getOperation());
        map.addValue("timestamp", event.getTimestamp());
        jdbcOperations.update(sqlQuery, map);
        return null;
    }

    @Override
    public void delete(long eventId) {
        checkEventId(eventId);
        final String sqlQuery = "delete from EVENTS " +
                "where EVENT_ID = :eventId";
        jdbcOperations.update(sqlQuery, Map.of("eventId", eventId));
    }

    private void checkEventId(long eventId) {
        final String sqlQuery = "select EVENT_ID " +
                "from EVENTS " +
                "where EVENT_ID = :eventId ";
        List<Long> eventsId = jdbcOperations.queryForList(sqlQuery, Map.of("eventId", eventId), Long.class);
        if (eventsId.size() != 1) {
            throw new EventNotFoundException(eventId);
        }
    }
}
