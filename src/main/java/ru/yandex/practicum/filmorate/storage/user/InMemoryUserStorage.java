package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    int idCounter = 1;
    final HashMap<Integer, User> usersMap = new HashMap<>();
    HashMap<Integer, Set<User>> usersFriends = new HashMap<>();

    @Override
    public User get(int userId) {
        if (!usersMap.containsKey(userId) || usersMap.get(userId) == null) {
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
        if (user.getId() != null && usersMap.containsKey(user.getId())) {
            throw new UserAlreadyExistException("Пользователь с ID=" + user.getId() + " уже есть в базе.");
        }
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
        if (!usersMap.containsKey(user.getId())) {
            throw new UserNotFoundException("Пользователь с ID=" + user.getId() + " не найден.");
        }
        usersMap.put(user.getId(), user);
        return user;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        if (!usersMap.containsKey(userId) || !usersMap.containsKey(friendId)) {
            throw new UserNotFoundException("Пользователь с ID=" + userId + " не найден.");
        }
        Set<User> uFriendIds = usersFriends.computeIfAbsent(userId, id -> new HashSet<>());
        uFriendIds.add(usersMap.get(friendId));

        Set<User> fFriendIds = usersFriends.computeIfAbsent(friendId, id -> new HashSet<>());
        fFriendIds.add(usersMap.get(userId));
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        if (!usersMap.containsKey(userId) || !usersMap.containsKey(friendId)) {
            throw new UserNotFoundException("Пользователь с ID=" + userId + " не найден.");
        }
        Set<User> uFriendIds = usersFriends.computeIfAbsent(userId, id -> new HashSet<>());
        uFriendIds.remove(usersMap.get(friendId));

        Set<User> fFriendIds = usersFriends.computeIfAbsent(friendId, id -> new HashSet<>());
        fFriendIds.remove(usersMap.get(userId));
    }

    @Override
    public List<User> getFriendsByUserId(int userId) {
        if (usersFriends.isEmpty()) {
            return new ArrayList<>();
        }
        if (!usersMap.containsKey(userId)) {
            throw new UserNotFoundException("Пользователь с ID=" + userId + " не найден.");
        }
        return new ArrayList<>(usersFriends.get(userId));
    }
}
