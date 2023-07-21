package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreRepository {

    Genre getById(long genreId);

    List<Genre> getGenresList();

    Genre add(Genre genre);

    //void addFromFilm(Film film);

    void delete(long genreId);

    Genre update(Genre genre);
}