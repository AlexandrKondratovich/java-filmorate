package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    int idCounter = 1;
    Map<Integer, Film> filmsMap = new HashMap<>();

    @Override
    public Film get(int filmId) {
        if (!filmsMap.containsKey(filmId)) {
            throw new FilmNotFoundException("Фильм с ID=" + filmId + " не найден.");
        }
        return filmsMap.get(filmId);
    }

    @Override
    public List<Film> getFilmsList() {
        return new ArrayList<>(filmsMap.values());
    }

    @Override
    public Film add(Film film) {
        film.setId(idCounter++);
        filmsMap.put(film.getId(), film);
        return film;
    }

    @Override
    public void delete(int filmId) {
        filmsMap.remove(filmId);
    }

    @Override
    public Film update(Film film) {
        get(film.getId());
        filmsMap.put(film.getId(), film);
        return film;
    }

    @Override
    public void addLike(int filmId, int userId) {
        get(filmId);
        filmsMap.get(filmId).getLikes().add((userId));
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        get(filmId);
        filmsMap.get(filmId).getLikes().remove((userId));
    }
}
