package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserComparator;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.ValidateService;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@Validated
@Slf4j
@RequiredArgsConstructor
public class UserController {
    final ValidateService validateService;
    final UserService userService;

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public User get(@PathVariable Integer userId) {
        return userService.get(userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Integer userId) {
        userService.delete(userId);
    }

    @PostMapping()
    public User add(@RequestBody @Valid User user) {
        validateService.validateUser(user);
        return userService.add(user);
    }

    @PutMapping()
    public User update(@RequestBody @Valid User user) {
        validateService.validateUser(user);
        return userService.update(user);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(@PathVariable int userId, @PathVariable int friendId) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable int userId, @PathVariable int friendId) {
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getFriendsById(@PathVariable int userId) {
        return userService.getFriendsListById(userId).stream()
                .sorted(new UserComparator())
                .collect(Collectors.toList()); //Для POSTMAN-проверки
    }

    @GetMapping("/{firstId}/friends/common/{secondId}")
    public List<User> getCommonFriendsList(@PathVariable int firstId, @PathVariable int secondId) {
        return userService.getCommonFriendsList(firstId, secondId).stream()
                .sorted(new UserComparator())
                .collect(Collectors.toList()); //Для POSTMAN-проверки
    }

    @GetMapping("/{userId}/recommendations")
    public List<Film> getRecommendations(@PathVariable long userId) {
        return userService.getRecommendations(userId);
    }
}
