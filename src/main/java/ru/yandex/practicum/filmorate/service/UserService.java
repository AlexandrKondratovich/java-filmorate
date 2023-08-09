package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventRepository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    @Qualifier("userDbRepository")
    final UserRepository userRepository;

    @Qualifier("eventDbRepository")
    final EventRepository eventRepository;


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
        eventRepository.deleteByUserId(userId);
    }

    public void addFriend(long userId, long friendId) {
        userRepository.addFriend(userId, friendId);
        eventRepository.add(EventRepository.createEvent(userId, EventType.FRIEND, friendId, Operation.ADD));
    }

    public void deleteFriend(long userId, long friendId) {
        userRepository.deleteFriend(userId, friendId);
        eventRepository.add(EventRepository.createEvent(userId, EventType.FRIEND, friendId, Operation.REMOVE));
    }

    public List<User> getFriendsListById(long userId) {
        return new ArrayList<>(userRepository.getFriendsByUserId(userId));
    }

    public List<User> getCommonFriendsList(long firstUserId, long secondUserId) {
        return userRepository.getCommonFriends(firstUserId, secondUserId);
    }

    public List<Event> getUserFeed(long userId) {
        userRepository.getById(userId);
        return eventRepository.getEventsByUserId(userId);
    }

    private User checkUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }
}