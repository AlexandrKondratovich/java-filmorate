package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorRepository {

    List<Director> getAllDirectors();

    Director getDirectorById(int id);

    Director addDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(int id);
}
