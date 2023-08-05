package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
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

    public List<Film> getFilmDirectorsSortedList(int directorId, String sortBy) {
        List<Film> filmList;
        if (sortBy.equals("year")) {
            filmList = filmRepository.getDirectorFilmListByYear(directorId);
        } else if (sortBy.equals("likes")) {
            filmList = filmRepository.getDirectorFilmListByLikes(directorId);
        } else {
            throw new IncorrectParameterException("В запрос передан неправильный параметр, нужен 'like' или 'year'");
        }
        return filmList;
    }

    public List<Film> searchFilm(String query, List<String> by) {
        if (by.size() == 2) {
            if ((by.get(0).equals("title") && by.get(1).equals("director")) || (by.get(0).equals("director") && (by.get(1).equals("title")))) {
                return filmRepository.searchFilmsByDirAndName(query);
            } else {
                throw new IncorrectParameterException("В запрос передан неправильный параметр, нужен 'director' и/или 'title'");
            }
        } else if (by.size() == 1) {
            if (by.get(0).equals("title")) {
                return filmRepository.searchFilmsByName(query);
            } else if (by.get(0).equals("director")) {
                return filmRepository.searchFilmsByDir(query);
            } else {
                throw new IncorrectParameterException("В запрос передан неправильный параметр, нужен 'director' и/или 'title'");
            }
        } else {
            throw new IncorrectParameterException("В запрос передан неправильный параметр, нужна строка для поиска и нужен параметр 'director' и/или 'title'");
        }
    }
}