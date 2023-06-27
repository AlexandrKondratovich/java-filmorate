package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.model.UserComparator;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
public class UserController {
    private HashMap<Integer, User> users = new HashMap<>();
    private int idCounter = 1;
    private ValidateService service = new ValidateService();

    @GetMapping("/users")
    public List<User> getAll() {
        ArrayList<User> list = new ArrayList<>(users.values());
        Collections.sort(list, new UserComparator());
        return list;
    }

    @PostMapping(value = "/users")
    public User add(@RequestBody @Valid User user) {
        service.validateUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        for (User existingUser: users.values()) {
            if (existingUser.getEmail().equals(user.getEmail())) {
                log.warn("UserAlreadyExistException: \"Пользователь с таким e-mail уже есть в базе.\"");
                throw new UserAlreadyExistException("Пользователь с таким e-mail уже есть в базе.");
            }
        }
        user.setId(idCounter++);
        users.put(user.getId(), user);
        log.info("Пользователь: {}", user);
        return user;
    }

    @PutMapping(value = "/users")
    public User update(@RequestBody @Valid User user) {
        service.validateUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (!users.containsKey(user.getId())) {
            log.warn("UserNotFoundException: \"Пользователя с таким ID нет в базе.\"");
            throw new UserNotFoundException("Пользователя с таким ID нет в базе.");
        }
        users.remove(user.getId());
        users.put(user.getId(), user);
        log.info("Пользователь: {}", user);
        return user;
    }


}
