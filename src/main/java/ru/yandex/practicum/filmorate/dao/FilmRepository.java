package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FilmRepository {

    Film getById(long filmId);

    List<Film> getFilmsList();

    Film add(Film film);

    void delete(long filmId);

    Film update(Film film);

    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);

    List<User> getFilmLikes(long filmId);

    List<Genre> getFilmGenres(long filmId);

    List<Film> getDirectorFilmListByYear(int directorId);

    List<Film> getDirectorFilmListByLikes(int directorId);

    List<Film> searchFilmsByDirAndName(String query);

    List<Film> searchFilmsByName(String query);

    List<Film> searchFilmsByDir(String query);

    List<Film> getMostPopularFilmsByYearAndGenre(long genreId, int year, int count);

    List<Film> getMostPopularFilmsByYear(int year, int count);

    List<Film> getMostPopularFilmsByGenre(long genreId, int count);

    List<Film> getMostPopularFilms(int count);

    List<Film> getRecommendations(long userId);

    List<Film> findByUserId(int userId);
}