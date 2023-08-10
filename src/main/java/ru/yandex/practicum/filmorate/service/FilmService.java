package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventRepository;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.dao.FilmRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    @Qualifier("filmDbRepository")
    final FilmRepository filmRepository;
    @Qualifier("eventDbRepository")
    final EventRepository eventRepository;

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
        eventRepository.add(EventRepository.createEvent(userId, EventType.LIKE, filmId, Operation.ADD));
    }

    public void deleteLike(long filmId, long userId) {
        filmRepository.deleteLike(filmId, userId);
        eventRepository.add(EventRepository.createEvent(userId, EventType.LIKE, filmId, Operation.REMOVE));
    }

    public List<User> getFIlmLikes(long filmId) {
        return filmRepository.getFilmLikes(filmId);
    }

    public List<Genre> getFilmGenres(long filmId) {
        return filmRepository.getFilmGenres(filmId);
    }

    public List<Film> getMostPopularFilms(Long genreId, Integer year, Integer count) {
        if (genreId != null) {
            if (year != null) {
                return filmRepository.getMostPopularFilmsByYearAndGenre(genreId, year, count);
            } else {
                return filmRepository.getMostPopularFilmsByGenre(genreId, count);
            }
        } else if (year != null) {
            return filmRepository.getMostPopularFilmsByYear(year, count);
        } else {
            return filmRepository.getMostPopularFilms(count);
        }
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
        if (by.isEmpty() || by.size() > 2) {
            throw new IncorrectParameterException("В запрос азпрос должено быть передано не менее 1 и не более 2 пораметров для выборки('director' и/или 'title')");
        }
        if (by.size() == 1 && by.contains("title")) {
            return filmRepository.searchFilmsByName(query);
        }
        if (by.size() == 1 && by.contains("director")) {
            return filmRepository.searchFilmsByDir(query);
        }
        if (by.contains("title") && by.contains("director")) {
            return filmRepository.searchFilmsByDirAndName(query);
        }
        throw new IncorrectParameterException("В запрос передан неправильный параметр, нужен 'director' и/или 'title'");
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        List<Film> userFilms = filmRepository.findByUserId(userId);
        List<Film> friendFilms = filmRepository.findByUserId(friendId);

        return userFilms.stream()
                .filter(friendFilms::contains).sorted(new TopFilmComparator()).collect(Collectors.toList());
    }
}