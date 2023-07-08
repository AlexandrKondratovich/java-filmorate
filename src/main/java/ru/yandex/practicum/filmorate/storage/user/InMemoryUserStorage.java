package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    int idCounter = 1;
    final Map<Integer, User> usersMap = new HashMap<>();

    @Override
    public User get(int userId) {
        if (!usersMap.containsKey(userId)) {
            throw new UserNotFoundException("Пользователь с ID=" + userId + " не найден.");
        }
        return usersMap.get(userId);
    }

    @Override
    public List<User> getUsersList() {
        return new ArrayList<>(usersMap.values());
    }

    @Override
    public User add(User user) {
        user.setId(idCounter++);
        usersMap.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(int userId) {
        usersMap.remove(userId);
    }

    @Override
    public User update(User user) {
        get(user.getId());
        usersMap.put(user.getId(), user);
        return user;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        get(userId);
        get(friendId);
        usersMap.get(userId).getFriends().add(friendId);
        usersMap.get(friendId).getFriends().add(userId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        get(userId);
        get(friendId);
        usersMap.get(userId).getFriends().remove(friendId);
        usersMap.get(friendId).getFriends().remove(userId);
    }

    @Override
    public List<User> getFriendsByUserId(int userId) {
        get(userId);
        List<User> friendsList = new ArrayList<>();
        for (Integer id : usersMap.get(userId).getFriends()) {
            friendsList.add(usersMap.get(id));
        }
        return friendsList;
    }
}
