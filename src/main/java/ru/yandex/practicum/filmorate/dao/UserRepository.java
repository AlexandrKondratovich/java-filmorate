package ru.yandex.practicum.filmorate.dao;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
public interface UserRepository {

    User getById(long userId);

    List<User> getUsersList();

    User add(User user);

    void delete(long userId);

    User update(User user);

    List<User> addFriend(long userFrom, long userTo);

    List<User> deleteFriend(long userFrom, long userTo);

    List<User> getFriendsByUserId(long userId);

    List<User> getCommonFriends(long firstUserId, long secondUserId);
}