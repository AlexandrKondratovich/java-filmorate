package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserComparator;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
public class UserController {
    private HashMap<String, User> users = new HashMap<>();
    private HashMap<Integer, String> emails = new HashMap<>();
    private int idCounter = 1;
    private ValidateService service = new ValidateService();

    @GetMapping("/users")
    public List<User> getAll(){
        ArrayList<User> list = new ArrayList<>(users.values());
        Collections.sort(list, new UserComparator());
        return list;
    }
    @PostMapping(value = "/users")
    public User add(@RequestBody @Valid User user) {
        service.validateUser(user);
        if (user.getName()==null || user.getName().isBlank()){
            user.setName(user.getLogin());
        }
        if (users.containsKey(user.getEmail())){
            log.info("UserAlreadyExistException: \"Пользователь с таким e-mail уже есть в базе.\"");
            throw new UserAlreadyExistException("Пользователь с таким e-mail уже есть в базе.");
        }
        user.setId(idCounter++);
        emails.put(user.getId(),user.getEmail());
        users.put(user.getEmail(), user);
        log.debug("Пользователь: {}", user);
        return user;
    }

    @PutMapping(value = "/users")
    public User update(@RequestBody @Valid User user) {
        service.validateUser(user);
        if (!emails.containsKey(user.getId())) {
            log.info("UserNotFoundException: \"Пользователя с таким ID нет в базе.\"");
            throw new UserNotFoundException("Пользователя с таким ID нет в базе.");
        }
        users.remove(emails.get(user.getId()));
        users.put(user.getEmail(), user);
        emails.put(user.getId(), user.getEmail());
        log.debug("Пользователь: {}", user);
        return user;
    }


}
