package ru.yandex.practicum.filmorate.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.MpaDbRepository;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@Import(MpaDbRepository.class)
public class MpaDbRepositoryTests {

    @Autowired
    private MpaDbRepository mpaDbRepository;

    @Test
    public void testGetById() {
        Mpa mpa = mpaDbRepository.getById(3);
        assertEquals(3, mpa.getId(), "Неверный ID");
        assertEquals("PG-13", mpa.getName(), "Неверное назавние.");
        assertThrows(MpaNotFoundException.class, () -> mpaDbRepository.getById(50L),
                "Неверный класс ошибки.");
    }

    @Test
    public void testGetMpaList() {
        List<Mpa> expectedList = new ArrayList<>();
        expectedList.add(new Mpa(1L, "G"));
        expectedList.add(new Mpa(2L, "PG"));
        expectedList.add(new Mpa(3L, "PG-13"));
        expectedList.add(new Mpa(4L, "R"));
        expectedList.add(new Mpa(5L, "NC-17"));

        List<Mpa> actualList = mpaDbRepository.getMpaList();
        assertTrue(expectedList.containsAll(actualList) && actualList.containsAll(expectedList),
                "Рейтинги не совпадают.");
    }

    @Test
    public void testAddMpa() {
        Mpa newMpa = new Mpa();
        newMpa.setName("RAT");
        newMpa.setId(mpaDbRepository.add(newMpa).getId());

        assertEquals(newMpa, mpaDbRepository.getById(newMpa.getId()), "Рейтинг не был добавлен.");
        assertEquals(6, mpaDbRepository.getMpaList().size(), "Неверное количество рейтингов.");
    }

    @Test
    public void testUpdateMpa() {
        Mpa newMpa = new Mpa();
        newMpa.setName("RAT");
        newMpa.setId(2L);
        mpaDbRepository.update(newMpa);

        assertEquals(newMpa, mpaDbRepository.getById(newMpa.getId()), "Рейтинг не был обновлен.");
        assertEquals(5, mpaDbRepository.getMpaList().size(), "Количество рейтингов было изменено.");
    }

    @Test
    public void testDeleteGenre() {
        mpaDbRepository.delete(5L);

        assertThrows(MpaNotFoundException.class, () -> mpaDbRepository.getById(5L),
                "Рейтинг не был удален");
        assertEquals(4, mpaDbRepository.getMpaList().size(), "Неверное количество рейтингов.");
    }
}