package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    final UserStorage userStorage;

    public User get(int userId) {
        return userStorage.get(userId);
    }

    public List<User> getAll() {
        return userStorage.getUsersList();
    }

    public User add(User user) {
        return userStorage.add(checkUserName(user));
    }

    public User update(User user) {
        return userStorage.update(checkUserName(user));
    }

    public void delete(int userId) {
        userStorage.delete(userId);
    }

    public void addFriend(int userId, int friendId) {
        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(int userId, int friendId) {
        userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getFriendsListById(int userId) {
        return userStorage.getFriendsByUserId(userId);
    }

    public List<User> getCommonFriendsList(int firstUserId, int secondUserId) {
        List<User> commonFriends = new ArrayList<>();
        if (userStorage.getFriendsByUserId(firstUserId).size() != 0) {
            for (User friend : userStorage.getFriendsByUserId(firstUserId)) {
                if (userStorage.getFriendsByUserId(secondUserId).contains(friend)) {
                    commonFriends.add(friend);
                }
            }
        }
        return commonFriends;
    }

    private User checkUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }
}
