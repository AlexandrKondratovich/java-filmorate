package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    int idCounter = 1;
    HashMap<Integer, Film> filmsMap = new HashMap<>();
    Map<Integer, Set<Integer>> filmsLikes = new HashMap<>();

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
        if (film.getId() != null && filmsMap.containsKey(film.getId())) {
            throw new FilmAlreadyExistException("Фильм с ID=" + film.getId() + " уже есть в базе.");
        }
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
        if (!filmsMap.containsKey(film.getId())) {
            throw new FilmNotFoundException("Фильм с ID=" + film.getId() + " не найден.");
        }
        filmsMap.put(film.getId(), film);
        return film;
    }

    @Override
    public void addLike(int filmId, int userId) {
        if (!filmsMap.containsKey(filmId)) {
            throw new FilmNotFoundException("Фильм с ID=" + filmId + " не найден.");
        }
        Set<Integer> filmLikes = filmsLikes.computeIfAbsent(filmId, id -> new HashSet<>());
        filmLikes.add(userId);
        filmsLikes.put(filmId, filmLikes);
        filmsMap.get(filmId).setLikes(filmsLikes.get(filmId));
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        if (!filmsMap.containsKey(filmId)) {
            throw new FilmNotFoundException("Фильм с ID=" + filmId + " не найден.");
        }
        Set<Integer> filmLikes = filmsLikes.computeIfAbsent(filmId, id -> new HashSet<>());
        filmLikes.remove(userId);
        filmsLikes.put(filmId, filmLikes);
        filmsMap.get(filmId).setLikes(filmsLikes.get(filmId));
    }

    @Override
    public Set<Integer> getLikesCountByFilmId(int filmId) {
        return filmsLikes.get(filmId);
    }
}
