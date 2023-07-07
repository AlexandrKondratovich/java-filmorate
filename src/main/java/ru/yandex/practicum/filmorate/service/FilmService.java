package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.TopFilmComparator;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FilmService {
    final FilmStorage filmStorage;
    final UserStorage userStorage;

    public Film get(int filmId) {
        return filmStorage.get(filmId);
    }

    public List<Film> getAll() {
        return filmStorage.getFilmsList();
    }

    public Film add(Film film) {
        filmStorage.add(film);
        return film;
    }

    public Film update(Film film) {
        filmStorage.update(film);
        return film;
    }

    public void delete(int filmId) {
        filmStorage.delete(filmId);
    }

    public void addLike(int filmId, int userId) {
        if (userStorage.get(userId) == null) {
            throw new UserNotFoundException();
        }
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        userStorage.get(userId);
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getTopFilms(Integer count) {
        TopFilmComparator comparator = new TopFilmComparator();
        List<Film> films = filmStorage.getFilmsList();
        films.sort(comparator);
        if (count == null) {
            if (films.size() > 10) {
                films.subList(0, 9);
            }
        } else {
            if (films.size() >= count) {
                films = films.subList(0, count);
            }
        }
        return films;
    }

}
