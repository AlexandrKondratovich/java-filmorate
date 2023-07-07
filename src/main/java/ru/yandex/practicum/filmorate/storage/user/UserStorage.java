package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
public interface UserStorage {

    User get(int userId);

    List<User> getUsersList();

    User add(User user);

    void delete(int userId);

    User update(User user);

    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    List<User> getFriendsByUserId(int userId);
}
