package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    UserController controller;

    @BeforeEach
    void setUp() {
        final ValidateService validateService = new ValidateService();
        final UserStorage userStorage = new InMemoryUserStorage();
        final UserService userService = new UserService(userStorage);
        controller = new UserController(validateService, userService);
    }

    @Test
    void shouldAddNewUser() {
        User newUser = new User();
        newUser.setLogin("login");
        newUser.setEmail("email@mail.ru");
        controller.add(newUser);
        assertEquals(1, controller.getAll().size(), "Кол-во пользователей неверное.");
        Assertions.assertEquals(1, controller.getAll().get(0).getId(), "Присвоен неверный ID.");
        Assertions.assertEquals("login", controller.getAll().get(0).getName(), "Присвоено неверное имя.");

        User oneMoreNewUser = new User();
        oneMoreNewUser.setLogin("login");
        oneMoreNewUser.setName("name");
        oneMoreNewUser.setEmail("email@yandex.ru");
        controller.add(oneMoreNewUser);
        assertEquals(2, controller.getAll().size(), "Кол-во пользователей неверное.");
        Assertions.assertEquals(2, controller.getAll().get(1).getId(), "Присвоен неверный ID.");
        Assertions.assertEquals("name", controller.getAll().get(1).getName(), "Присвоено неверное имя.");
    }

    @Test
    void shouldRejectIncorrectLoginUser() {
        User newUser = new User();
        newUser.setLogin(" ");
        newUser.setEmail("email@mail.ru");
        assertThrows(RuntimeException.class, () -> controller.add(newUser));
        assertEquals(0, controller.getAll().size(), "Кол-во пользователей неверное.");
    }

    @Test
    void shouldRejectIncorrectEmailUser() {
        User newUser = new User();
        newUser.setLogin("login");
        newUser.setEmail("email.ru");
        assertThrows(RuntimeException.class, () -> controller.add(newUser));
        assertEquals(0, controller.getAll().size(), "Кол-во пользователей неверное.");
    }

    @Test
    void shouldRejectIncorrectBirthdayUser() {
        User newUser = new User();
        newUser.setLogin("login");
        newUser.setEmail("email@mail.ru");
        newUser.setBirthday(LocalDate.of(2040,1,1));
        assertThrows(RuntimeException.class, () -> controller.add(newUser));
        assertEquals(0, controller.getAll().size(), "Кол-во пользователей неверное.");
    }

    @Test
    void shouldUpdateUser() {
        User newUser = new User();
        newUser.setLogin("login");
        newUser.setEmail("email@mail.ru");
        newUser.setBirthday(LocalDate.of(1990,8,15));
        controller.add(newUser);

        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setLogin("newLogin");
        updatedUser.setName("newName");
        updatedUser.setEmail("yandex@mail.ru");
        updatedUser.setBirthday(LocalDate.of(1980,12,25));
        controller.update(updatedUser);
        assertEquals(1, controller.getAll().size(), "Кол-во пользователей неверное.");
        Assertions.assertEquals(1, controller.getAll().get(0).getId(), "Присвоен неверный ID.");
        Assertions.assertEquals("newName", controller.getAll().get(0).getName(), "Присвоено неверное имя.");
        Assertions.assertEquals("newLogin", controller.getAll().get(0).getLogin(), "Присвоен неверный логин.");
        Assertions.assertEquals(LocalDate.of(1980,12,25), controller.getAll().get(0).getBirthday(),
                "Присвоена неверная дата рождения");
    }

    @Test
    void shouldReturnUsersList() {
        User newUser = new User();
        newUser.setLogin("login");
        newUser.setEmail("email@mail.ru");
        newUser.setBirthday(LocalDate.of(1990,8,15));
        controller.add(newUser);
        newUser.setId(1);

        User oneMoreNewUser = new User();
        oneMoreNewUser.setLogin("newLogin");
        oneMoreNewUser.setName("newName");
        oneMoreNewUser.setEmail("yandex@mail.ru");
        oneMoreNewUser.setBirthday(LocalDate.of(1980,12,25));
        controller.add(oneMoreNewUser);
        oneMoreNewUser.setId(2);

        List<User> expectedList = new ArrayList<>();
        expectedList.add(newUser);
        expectedList.add(oneMoreNewUser);
        assertEquals(expectedList, controller.getAll(), "Списки е совпадают.");
    }
}
