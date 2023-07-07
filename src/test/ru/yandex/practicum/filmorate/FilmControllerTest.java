package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {
    FilmController controller;
    final UserStorage userStorage = new InMemoryUserStorage();
    final FilmStorage filmStorage = new InMemoryFilmStorage();


    @BeforeEach
    void setUp() {
        controller = new FilmController(new ValidateService(), new FilmService(filmStorage, userStorage));
    }

    @Test
    void shouldAddNewFilm() {
        Film newFilm = new Film();
        newFilm.setName("name");
        controller.add(newFilm);
        assertEquals(1, controller.getAll().size(), "Кол-во фильмов неверное.");
        assertEquals(1, controller.getAll().get(0).getId(), "Присвоен неверный ID.");

        Film oneMoreNewFilm = new Film();
        oneMoreNewFilm.setName("name");
        controller.add(oneMoreNewFilm);
        assertEquals(2, controller.getAll().size(), "Кол-во фильмов неверное.");
        assertEquals(2, controller.getAll().get(1).getId(), "Присвоен неверный ID.");
    }

    @Test
    void shouldRejectIncorrectNameFilm() {
        Film newFilm = new Film();
        newFilm.setName(" ");
        assertThrows(RuntimeException.class, () -> controller.add(newFilm));
        assertEquals(0, controller.getAll().size(), "Кол-во фильмов неверное.");
    }

    @Test
    void shouldRejectIncorrectDurationFilm() {
        Film newFilm = new Film();
        newFilm.setName("name");
        newFilm.setDuration(-10L);
        assertThrows(RuntimeException.class, () -> controller.add(newFilm));
        assertEquals(0, controller.getAll().size(), "Кол-во фильмов неверное.");
    }

    @Test
    void shouldRejectIncorrectReleaseDateFilm() {
        Film newFilm = new Film();
        newFilm.setName("name");
        newFilm.setReleaseDate(LocalDate.of(1800,1,1));
        assertThrows(RuntimeException.class, () -> controller.add(newFilm));
        assertEquals(0, controller.getAll().size(), "Кол-во фильмов неверное.");
    }

    @Test
    void shouldUpdateFilm() {
        Film film = new Film();
        film.setName("name");
        controller.add(film);
        Film update = new Film();
        update.setId(1);
        update.setName("newName");
        update.setDuration(100L);
        controller.update(update);
        assertEquals("newName", controller.getAll().get(0).getName(),"Не было обновлено название.");
        assertEquals(100L, controller.getAll().get(0).getDuration(),"Не было обновлено название.");
        assertEquals(1, controller.getAll().size(), "Кол-во фильмов неверное.");
        assertEquals(1, controller.getAll().get(0).getId(), "Присвоен неверный ID.");
    }

    @Test
    void shouldReturnFilmsList() {
        Film newFilm = new Film();
        newFilm.setName("name");
        controller.add(newFilm);
        Film oneMoreNewFilm = new Film();
        oneMoreNewFilm.setName("name");
        controller.add(oneMoreNewFilm);
        newFilm.setId(1);
        oneMoreNewFilm.setId(2);
        List<Film> expectedList = new ArrayList<>();
        expectedList.add(newFilm);
        expectedList.add(oneMoreNewFilm);
        assertEquals(expectedList, controller.getAll(), "Списки не совпадают");
    }

}
