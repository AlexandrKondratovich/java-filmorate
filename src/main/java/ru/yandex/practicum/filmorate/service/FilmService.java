package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.TopFilmComparator;
import ru.yandex.practicum.filmorate.dao.FilmRepository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FilmService {
    @Qualifier("filmDbRepository")
    final FilmRepository filmRepository;

    public Film get(long filmId) {
        return filmRepository.getById(filmId);
    }

    public List<Film> getAll() {
        return new ArrayList<>(filmRepository.getFilmsList());
    }

    public Film add(Film film) {
        filmRepository.add(film);
        return get(film.getId());
    }

    public Film update(Film film) {
        filmRepository.update(film);
        return get(film.getId());
    }

    public void delete(long filmId) {
        filmRepository.delete(filmId);
    }

    public void addLike(long filmId, long userId) {
        filmRepository.addLike(filmId, userId);
    }

    public void deleteLike(long filmId, long userId) {
        filmRepository.deleteLike(filmId, userId);
    }

    public List<User> getFIlmLikes(long filmId) {
        return filmRepository.getFilmLikes(filmId);
    }

    public List<Genre> getFilmGenres(long filmId) {
        return filmRepository.getFilmGenres(filmId);
    }

    public List<Film> getTopFilms(Integer count) {
        TopFilmComparator comparator = new TopFilmComparator();
        List<Film> films = new ArrayList<>(filmRepository.getFilmsList());
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