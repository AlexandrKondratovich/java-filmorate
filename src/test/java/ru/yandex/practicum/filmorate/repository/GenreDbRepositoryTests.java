package ru.yandex.practicum.filmorate.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.GenreDbRepository;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@JdbcTest
@Import(GenreDbRepository.class)
public class GenreDbRepositoryTests {

    @Autowired
    private GenreDbRepository genreDbRepository;

    @Test
    public void testGetById() {
        Genre genre = genreDbRepository.getById(3);
        assertEquals(3, genre.getId(), "Неверный ID");
        assertEquals("Мультфильм", genre.getName(), "Неверное назавние.");
        assertThrows(GenreNotFoundException.class, () -> genreDbRepository.getById(50L),
                "Неверный класс ошибки.");
    }

    @Test
    public void testGetGenresList() {
        List<Genre> expectedList = new ArrayList<>();
        expectedList.add(new Genre(1, "Комедия"));
        expectedList.add(new Genre(2, "Драма"));
        expectedList.add(new Genre(3, "Мультфильм"));
        expectedList.add(new Genre(4, "Триллер"));
        expectedList.add(new Genre(5, "Документальный"));
        expectedList.add(new Genre(6, "Боевик"));

        List<Genre> actualList = genreDbRepository.getGenresList();
        assertTrue(expectedList.containsAll(actualList) && actualList.containsAll(expectedList),
                "Жанры не совпадают.");
    }

    @Test
    public void testAddGenre() {
        Genre newGenre = new Genre();
        newGenre.setName("Мюзикл");
        newGenre.setId(genreDbRepository.add(newGenre).getId());

        assertEquals(newGenre, genreDbRepository.getById(newGenre.getId()), "Жанр не был добавлен.");
        assertEquals(7, genreDbRepository.getGenresList().size(), "Неверное количество жанров.");
    }

    @Test
    public void testUpdateGenre() {
        Genre newGenre = new Genre();
        newGenre.setName("Мюзикл");
        newGenre.setId(3L);
        genreDbRepository.update(newGenre);

        assertEquals(newGenre, genreDbRepository.getById(newGenre.getId()), "Жанр не был обновлен.");
        assertEquals(6, genreDbRepository.getGenresList().size(), "Количество жанров было изменено.");
    }

    @Test
    public void testDeleteGenre() {
        genreDbRepository.delete(5L);

        assertThrows(GenreNotFoundException.class, () -> genreDbRepository.getById(5L),
                "Жанр не был удален");
        assertEquals(5, genreDbRepository.getGenresList().size(), "Неверное количество жанров.");
    }
}