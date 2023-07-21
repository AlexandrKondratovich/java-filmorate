package ru.yandex.practicum.filmorate.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import ru.yandex.practicum.filmorate.dao.UserDbRepository;
import ru.yandex.practicum.filmorate.dao.UserRepository;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import(UserDbRepository.class)
class UserDbRepositoryTest {

    @Autowired
    private UserRepository userRepository;


    @Test
    public void testFindUserById() {
        User user = userRepository.getById(1L);

        assertNotNull(user);
        assertEquals(1, user.getId(), "Неверный ID.");
        assertEquals("email@email.com", user.getEmail(), "Неверный E-mail.");
        assertEquals("user", user.getLogin(), "Неврный логин.");
        assertEquals("test", user.getName(), "Неверное имя.");
        assertEquals(LocalDate.of(2000, 3, 22),
                user.getBirthday(),
                "Неверная дата рождения.");

        assertThrows(UserNotFoundException.class,
                () -> userRepository.getById(3L),
                "Неверный класс ошибки.");
    }

    @Test
    public void testGetUsersList() {
        List<User> expectedList = new ArrayList<>();
        expectedList.add(userRepository.getById(1L));
        expectedList.add(userRepository.getById(2L));
        List<User> actualList = userRepository.getUsersList();

        assertTrue(expectedList.containsAll(actualList) && actualList.containsAll(expectedList),
                "Пользователи не совпадают.");
    }

    @Test
    public void testAddUser() {
        User newUser = new User();
        newUser.setEmail("user3mail@email.com");
        newUser.setLogin("user3Login");
        newUser.setName("name3");
        newUser.setBirthday(LocalDate.of(1998, 10, 10));
        newUser.setId(userRepository.add(newUser).getId());

        assertEquals(newUser, userRepository.getById(newUser.getId()));
        assertEquals(3, newUser.getId(), "Неверный ID.");
        assertEquals("user3mail@email.com", newUser.getEmail(), "Неверный E-mail.");
        assertEquals("user3Login", newUser.getLogin(), "Неврный логин.");
        assertEquals("name3", newUser.getName(), "Неверное имя.");
        assertEquals(LocalDate.of(1998, 10, 10),
                newUser.getBirthday(),
                "Неверная дата рождения.");

        User wrongUser = new User();
        assertThrows(DataIntegrityViolationException.class,
                () -> userRepository.add(wrongUser),
                "Неверный класс ошибки.");
    }

    @Test
    public void testDelete() {
        List<User> expectedList = new ArrayList<>();
        expectedList.add(userRepository.getById(1L));
        expectedList.add(userRepository.getById(2L));

        userRepository.delete(1L);
        expectedList.remove(0);
        assertEquals(expectedList, userRepository.getUsersList(), "Списки не совпадают.");
        assertEquals(1, userRepository.getUsersList().size(), "Размеры списков не совпадают.");
        assertEquals(expectedList.get(0), userRepository.getById(2L), "Пользователи не совпадают.");

        userRepository.delete(2L);
        assertEquals(0, userRepository.getUsersList().size(), "Размеры списков не совпадают.");
    }

    @Test
    public void testUpdate() {
        User user = userRepository.getById(1L);
        user.setName("New name");
        user.setBirthday(LocalDate.of(2000, 10, 4));
        userRepository.update(user);

        assertEquals(1, userRepository.getById(1L).getId(), "Неверный ID.");
        assertEquals("email@email.com", userRepository.getById(1L).getEmail(), "Неверный E-mail.");
        assertEquals("user", userRepository.getById(1L).getLogin(), "Неврный логин.");
        assertEquals("New name", userRepository.getById(1L).getName(), "Неверное имя.");
        assertEquals(LocalDate.of(2000, 10, 4),
                userRepository.getById(1L).getBirthday(),
                "Неверная дата рождения");
    }

    @Test
    public void testAddFriend() {
        userRepository.addFriend(1L, 2L);
        List<User> expectedFriendList = List.of(userRepository.getById(2L));
        assertEquals(expectedFriendList, userRepository.getFriendsByUserId(1L), "Списки друзей не совпадают.");

        expectedFriendList = List.of(userRepository.getById(1L));
        assertNotEquals(expectedFriendList, userRepository.getFriendsByUserId(2L), "Список друзей не пустой.");

        assertThrows(UserNotFoundException.class,
                () -> userRepository.addFriend(5L,4L),
                "Неверный класс ошибки.");

        assertThrows(RuntimeException.class,
                () -> userRepository.addFriend(1L,1L),
                "Неверный класс ошибки.");
    }

    @Test
    public void testDeleteFriend() {
        userRepository.addFriend(1L, 2L);
        List<User> expectedFriendList = List.of(userRepository.getById(2L));
        assertEquals(expectedFriendList, userRepository.getFriendsByUserId(1L), "Списки друзей не совпадают.");

        userRepository.deleteFriend(1L, 2L);
        expectedFriendList = new ArrayList<>();
        assertEquals(expectedFriendList, userRepository.getFriendsByUserId(1L), "Список друзей не пустой.");

        assertThrows(UserNotFoundException.class,
                () -> userRepository.deleteFriend(5L,4L),
                "Неверный класс ошибки.");

        assertThrows(RuntimeException.class,
                () -> userRepository.deleteFriend(2L,1L),
                "Неверный класс ошибки.");
    }

    @Test
    public void testGetFriendsByUserId() {
        List<User> expectedList = new ArrayList<>();

        userRepository.addFriend(1L, 2L);
        expectedList.add(userRepository.getById(2L));

        User newUser = new User();
        newUser.setEmail("user3mail@email.com");
        newUser.setLogin("user3Login");
        newUser.setName("name3");
        newUser.setBirthday(LocalDate.of(1998, 10, 10));
        newUser.setId(userRepository.add(newUser).getId());

        userRepository.addFriend(1L, newUser.getId());
        expectedList.add(userRepository.getById(newUser.getId()));

        assertEquals(expectedList, userRepository.getFriendsByUserId(1L), "Списки не совпадают.");
        assertEquals(2, userRepository.getFriendsByUserId(1L).size(), "Размеры списков не совпадают.");
    }

    @Test
    public void testGetCommonFriends() {

        System.out.println(userRepository.getUsersList());

        User newUser1 = new User();
        newUser1.setEmail("user3mail@email.com");
        newUser1.setLogin("user3Login");
        newUser1.setName("name3");
        newUser1.setBirthday(LocalDate.of(1998, 10, 10));
        newUser1.setId(userRepository.add(newUser1).getId());

        System.out.println(userRepository.getUsersList());

        User newUser2 = new User();
        newUser2.setEmail("user4mail@email.com");
        newUser2.setLogin("user4Login");
        newUser2.setName("name4");
        newUser2.setBirthday(LocalDate.of(1978, 1, 14));
        newUser2.setId(userRepository.add(newUser2).getId());

        System.out.println(userRepository.getUsersList());

        userRepository.addFriend(1L, 2L);
        userRepository.addFriend(1L, newUser2.getId());
        userRepository.addFriend(2L, 1L);
        userRepository.addFriend(2L, newUser1.getId());
        userRepository.addFriend(newUser1.getId(), 2L);
        userRepository.addFriend(newUser1.getId(), newUser2.getId());
        userRepository.addFriend(newUser2.getId(), 1L);
        userRepository.addFriend(newUser2.getId(), 2L);
        userRepository.addFriend(newUser2.getId(), newUser1.getId());

        //1 -> 2,nU2;     2 -> 1,nU1;     nU1 -> 2,nU2;     nU2 -> 1,2,nU1;

        //1:2=0; 1:nU1=2,nU2; 1:nU2=2; 2:nU1=0; 2:nU2=1,nU1; nU1:nU2=2;

        assertEquals(new ArrayList<>(), userRepository.getCommonFriends(1L, 2L), "Списки(1:2) не совпадают.");
        assertEquals(0, userRepository.getCommonFriends(1L, 2L).size(), "Неверный размер списка(1:2).");

        assertEquals(List.of(userRepository.getById(2L), userRepository.getById(newUser2.getId())),
                userRepository.getCommonFriends(1L, newUser1.getId()), "Списки(1:3) не совпадают.");
        assertEquals(2, userRepository.getCommonFriends(1L, newUser1.getId()).size(),
                "Неверный размер списка(1:3).");

        assertEquals(List.of(userRepository.getById(2L)),
                userRepository.getCommonFriends(1L, newUser2.getId()), "Списки(1:4) не совпадают.");
        assertEquals(1, userRepository.getCommonFriends(1L, newUser2.getId()).size(),
                "Неверный размер списка(1:4).");

        assertEquals(new ArrayList<>(), userRepository.getCommonFriends(2L, newUser1.getId()),
                "Списки(2:3) не совпадают.");
        assertEquals(0, userRepository.getCommonFriends(2L, newUser1.getId()).size(),
                "Неверный размер списка(2:3).");

        assertEquals(List.of(userRepository.getById(1L), userRepository.getById(newUser1.getId())),
                userRepository.getCommonFriends(2L, newUser2.getId()), "Списки(2:4) не совпадают.");
        assertEquals(2, userRepository.getCommonFriends(2L, newUser2.getId()).size(),
                "Неверный размер списка(2:4).");

        assertEquals(List.of(userRepository.getById(2L)),
                userRepository.getCommonFriends(newUser1.getId(), newUser2.getId()),
                "Списки(3:4) не совпадают.");
        assertEquals(1, userRepository.getCommonFriends(newUser1.getId(), newUser2.getId()).size(),
                "Неверный размер списка(3:4).");
    }
}