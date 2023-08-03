package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorRepository {

    List<Director> getAllDirectors();

    Optional<Director> getDirectorById(int id);

    Director addDirector(Director director);

    Director updateDirector(Director director);

    boolean deleteDirector(int id);
}
