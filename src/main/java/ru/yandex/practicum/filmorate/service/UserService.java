package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmRepository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    @Qualifier("userDbRepository")
    final UserRepository userRepository;
    @Qualifier("filmDbRepository")
    final FilmRepository filmRepository;

    public User get(long userId) {
        return userRepository.getById(userId);
    }

    public List<User> getAll() {
        return new ArrayList<>(userRepository.getUsersList());
    }

    public User add(User user) {
        return userRepository.add(checkUserName(user));
    }

    public User update(User user) {
        return userRepository.update(checkUserName(user));
    }

    public void delete(long userId) {
        userRepository.delete(userId);
    }

    public void addFriend(long userId, long friendId) {
        userRepository.addFriend(userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        userRepository.deleteFriend(userId, friendId);
    }

    public List<User> getFriendsListById(long userId) {
        return new ArrayList<>(userRepository.getFriendsByUserId(userId));
    }

    public List<User> getCommonFriendsList(long firstUserId, long secondUserId) {
        return userRepository.getCommonFriends(firstUserId, secondUserId);
    }

    private User checkUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }

    public List<Film> getRecommendations(long userId) {
        return filmRepository.getRecommendations(userId);
    }
}