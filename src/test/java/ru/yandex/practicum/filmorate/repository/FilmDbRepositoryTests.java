package ru.yandex.practicum.filmorate.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.FilmDbRepository;
import ru.yandex.practicum.filmorate.dao.FilmRepository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import(FilmDbRepository.class)
public class FilmDbRepositoryTests {

    @Autowired
    private FilmRepository filmRepository;

    @Test
    public void testGetById() {
        Film film = filmRepository.getById(1L);

        assertNotNull(film);
        assertEquals(1, film.getId(), "Неверный ID.");
        assertEquals("name1", film.getName(), "Неверное название.");
        assertEquals("description1", film.getDescription(), "Неверное описание.");
        assertEquals(new Mpa(3L, "PG-13"), film.getMpa(), "Неверный рейтинг.");
        assertEquals(LocalDate.of(2004, 4, 19),
                film.getReleaseDate(),
                "Неверная дата релиза.");

        assertThrows(FilmNotFoundException.class,
                () -> filmRepository.getById(100L),
                "Неверный класс ошибки.");
    }

    @Test
    public void testGetFilmsList() {
        List<Film> expectedList = new ArrayList<>();
        expectedList.add(filmRepository.getById(1L));
        expectedList.add(filmRepository.getById(2L));
        List<Film> actualList = filmRepository.getFilmsList();

        assertTrue(expectedList.containsAll(actualList) && actualList.containsAll(expectedList),
                "Фильмы не совпадают.");
    }

    @Test
    public void testAddFilm() {
        Film newFilm = new Film();
        newFilm.setName("name3");
        newFilm.setDescription("description3");
        newFilm.setMpa(new Mpa(1L, null));
        newFilm.setReleaseDate(LocalDate.of(1998, 10, 10));
        newFilm.setId(filmRepository.add(newFilm).getId());

        assertEquals(newFilm, filmRepository.getById(newFilm.getId()));
        assertEquals("name3", newFilm.getName(), "Неверное название.");
        assertEquals("description3", newFilm.getDescription(), "Неверное описание.");
        assertEquals(new Mpa(1L, "G"), newFilm.getMpa(), "Неверное имя.");
        assertEquals(LocalDate.of(1998, 10, 10),
                newFilm.getReleaseDate(),
                "Неверная дата рождения.");

        Film wrongFilm = new Film();
        assertThrows(NullPointerException.class,
                () -> filmRepository.add(wrongFilm),
                "Неверный класс ошибки.");
    }

    @Test
    public void testDelete() {
        List<Film> expectedList = new ArrayList<>();
        expectedList.add(filmRepository.getById(1L));
        expectedList.add(filmRepository.getById(2L));

        filmRepository.delete(1L);
        expectedList.remove(0);
        assertEquals(expectedList, filmRepository.getFilmsList(), "Списки не совпадают.");
        assertEquals(1, filmRepository.getFilmsList().size(), "Размеры списков не совпадают.");
        assertTrue(expectedList.containsAll(filmRepository.getFilmsList())
                && filmRepository.getFilmsList().containsAll(expectedList), "Пользователи не совпадают.");
    }

    @Test
    public void testUpdate() {
        Film film = filmRepository.getById(1L);
        film.setName("New name");
        film.setDescription("New descr");
        film.setReleaseDate(LocalDate.of(2000, 10, 4));
        filmRepository.update(film);

        assertEquals(1, filmRepository.getById(1L).getId(), "Неверный ID.");
        assertEquals("New name", filmRepository.getById(1L).getName(), "Неверное название.");
        assertEquals("New descr", filmRepository.getById(1L).getDescription(),
                "Неверное описание.");
        assertEquals(new Mpa(3L, "PG-13"), filmRepository.getById(1L).getMpa(), "Неверный рейтинг.");
        assertEquals(LocalDate.of(2000, 10, 4),
                filmRepository.getById(1L).getReleaseDate(),
                "Неверная дата релиза");
    }

    @Test
    public void testAddAndDeleteLike() {
        assertEquals(0, filmRepository.getFilmLikes(1L).size(),
                "Изначальный спискок лайков не пустой.");
        List<Long> expectedList = new ArrayList<>();
        filmRepository.addLike(1L, 1L);
        expectedList.add(1L);
        List<Long> actualList = filmRepository.getFilmLikes(1L).stream()
                .map(User::getId)
                .collect(Collectors.toList());
        assertEquals(1, filmRepository.getFilmLikes(1L).size(),
                "Лайк не был добален");
        assertTrue(expectedList.containsAll(actualList) && actualList.containsAll(expectedList),
                "Добавлен лайк неверного пользователя.");

        filmRepository.addLike(1L, 2L);
        expectedList.add(2L);
        actualList = filmRepository.getFilmLikes(1L).stream()
                .map(User::getId)
                .collect(Collectors.toList());
        assertEquals(2, filmRepository.getFilmLikes(1L).size(),
                "Лайк не был добален");
        assertTrue(expectedList.containsAll(actualList) && actualList.containsAll(expectedList),
                "Добавлен лайк неверного пользователя.");

        filmRepository.deleteLike(1L, 1L);
        expectedList.remove(1L);
        actualList = filmRepository.getFilmLikes(1L).stream()
                .map(User::getId)
                .collect(Collectors.toList());
        assertEquals(1, filmRepository.getFilmLikes(1L).size(),
                "Лайк не был удален");
        assertTrue(expectedList.containsAll(actualList) && actualList.containsAll(expectedList),
                "Удален лайк неверного пользователя.");
    }

    @Test
    public void testGetFilmLikes() {
        filmRepository.addLike(1L, 2L);

        filmRepository.addLike(2L, 1L);
        filmRepository.addLike(2L, 2L);

        List<Long> expectedList = new ArrayList<>();
        expectedList.add(2L);
        List<Long> actualList = filmRepository.getFilmLikes(1L).stream()
                .map(User::getId).collect(Collectors.toList());
        assertTrue(expectedList.containsAll(actualList) && actualList.containsAll(expectedList),
                "Список лайков неверный.");

        expectedList.add(1L);
        actualList = filmRepository.getFilmLikes(2L).stream()
                .map(User::getId).collect(Collectors.toList());
        assertTrue(expectedList.containsAll(actualList) && actualList.containsAll(expectedList),
                "Список лайков неверный.");
    }

    @Test
    public void testGetFilmGenres() {
        List<Long> expectedList = List.of(1L,3L,2L);
        List<Long> actualList = filmRepository.getFilmGenres(1L).stream()
                .map(Genre::getId).collect(Collectors.toList());

        assertTrue(expectedList.containsAll(actualList) && actualList.containsAll(expectedList),
                "Списки не совпадают.");
    }
}