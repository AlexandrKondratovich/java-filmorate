package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserDbRepository implements UserRepository {

    private final NamedParameterJdbcOperations jdbcOperations;

    public UserDbRepository(NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public User getById(long userId) {
        checkUserId(userId);
        final String sqlQuery = "select * " +
                        "from USERS " +
                        "where USER_ID = :userId";
        final List<User> users = jdbcOperations.query(sqlQuery, Map.of("userId", userId), new UserRowMapper());
        return users.get(0);
    }

    @Override
    public List<User> getUsersList() {
        final String sqlQuery = "select * " +
                "from USERS " +
                "order by USER_ID";
        return jdbcOperations.query(sqlQuery, new UserRowMapper());
    }

    @Override
    public User add(User user) {
        final String sqlQuery = "insert into USERS(EMAIL, LOGIN, NAME, BIRTHDAY) " +
                "values (:email, :login, :name, :birthday)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("email", user.getEmail());
        map.addValue("login", user.getLogin());
        map.addValue("name", user.getName());
        map.addValue("birthday", user.getBirthday());
        jdbcOperations.update(sqlQuery, map, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        return getById(user.getId());
    }

    @Override
    public void delete(long userId) {
        checkUserId(userId);
        String sqlQuery = "delete from USERS " +
                        "where USER_ID = :userId";
        jdbcOperations.update(sqlQuery, Map.of("userId", userId));
        sqlQuery = "delete from LIKES " +
                "where USER_ID = :userId";
        jdbcOperations.update(sqlQuery,Map.of("userId", userId));

        sqlQuery = "delete from LIKES " +
                "where USER_ID = :userId";
        jdbcOperations.update(sqlQuery,Map.of("userId", userId));
    }

    @Override
    public User update(User user) {
        checkUserId(user.getId());
        final String sqlQuery = "update USERS " +
                        "set EMAIL = :email, " +
                            "LOGIN = :login, " +
                            "NAME = :name, " +
                            "BIRTHDAY = :birthday " +
                        "where USER_ID = :userId";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("userId", user.getId());
        map.addValue("email", user.getEmail());
        map.addValue("login", user.getLogin());
        map.addValue("name", user.getName());
        map.addValue("birthday", user.getBirthday());
        jdbcOperations.update(sqlQuery, map);
        return getById(user.getId());
    }

    @Override
    public List<User> addFriend(long userFromId, long userToId) {
        checkUserId(userFromId);
        checkUserId(userToId);
        if (userFromId == userToId) {
            throw new RuntimeException("Пользователь не может добавить себя в друзья.");
        }
        String sqlQuery = "select USER_TO " +
                "from FRIENDSHIP_REQUESTS " +
                "where USER_FROM = :userFrom and USER_TO = :userTo";
        if (jdbcOperations.queryForList(sqlQuery, Map.of("userFrom",
                userFromId, "userTo", userToId)).size() != 0) {
            throw new RuntimeException("Такой запрос дружбы уже существует.");
        }
        sqlQuery = "insert into FRIENDSHIP_REQUESTS(USER_FROM, USER_TO) " +
                        "values (:userFrom, :userTo)";
        jdbcOperations.update(sqlQuery, Map.of("userFrom", userFromId, "userTo", userToId));
        return getFriendsByUserId(userFromId);
    }

    @Override
    public List<User> deleteFriend(long userFromId, long userToId) {
        checkUserId(userFromId);
        checkUserId(userToId);
        String sqlQuery = "select * " +
                "from FRIENDSHIP_REQUESTS " +
                "where USER_FROM = :userFrom AND USER_TO = :userTo";
        if (jdbcOperations.queryForMap(sqlQuery, Map.of("userFrom",
                userFromId, "userTo", userToId)).size() == 0) {
            throw new RuntimeException("Такой запрос дружбы отсутствует.");
        }
        sqlQuery = "delete from FRIENDSHIP_REQUESTS " +
                "where USER_FROM = :userFrom AND USER_TO = :userTo";
        jdbcOperations.update(sqlQuery, Map.of("userFrom", userFromId, "userTo", userToId));

        return getFriendsByUserId(userFromId);
    }

    @Override
    public List<User> getFriendsByUserId(long userId) {
        checkUserId(userId);
        final String sqlQuery = "select * " +
                "from USERS " +
                "where USER_ID in (" +
                                   "select USER_TO " +
                                   "from FRIENDSHIP_REQUESTS " +
                                   "where USER_FROM = :userId) " +
                "order by USER_ID";
        return jdbcOperations.query(sqlQuery, Map.of("userId", userId), new UserRowMapper());
    }

    private void checkUserId(long userId) {
        final String sqlQuery = "select USER_ID " +
                "from USERS " +
                "where USER_ID = :userId ";
        List<Long> usersId = jdbcOperations.queryForList(sqlQuery, Map.of("userId", userId), Long.class);
        if (usersId.size() != 1) {
            throw new UserNotFoundException(userId);
        }
    }

    @Override
    public List<User> getCommonFriends(long firstUserId, long secondUserId) {
        final String sqlQuery = "select USER_TO " +
                "from FRIENDSHIP_REQUESTS " +
                "where USER_FROM = :firstUserId and USER_TO in (" +
                                   "select USER_TO " +
                                   "from FRIENDSHIP_REQUESTS " +
                                   "where USER_FROM = :secondUserId)";
        List<Long> commonFriendsIds = jdbcOperations.queryForList(sqlQuery,
                Map.of("firstUserId", firstUserId, "secondUserId", secondUserId),
                Long.class);
        return commonFriendsIds.stream().map(this::getById).collect(Collectors.toList());
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(rs.getLong("USER_ID"),
                    rs.getString("EMAIL"),
                    rs.getString("LOGIN"),
                    rs.getString("NAME"),
                    rs.getDate("BIRTHDAY").toLocalDate());
        }
    }
}