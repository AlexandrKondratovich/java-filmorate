package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.rowMapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Component
public class GenreDbRepository implements GenreRepository {

    private final NamedParameterJdbcOperations jdbcOperations;

    public GenreDbRepository(NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public Genre getById(long genreId) {
        final String sqlQuery = "select * " +
                "from GENRES " +
                "where GENRE_ID = :genreId ";
        final List<Genre> genres = jdbcOperations.query(sqlQuery, Map.of("genreId", genreId), new GenreRowMapper());
        if (genres.size() != 1) {
            throw new GenreNotFoundException(genreId);
        }
        return genres.get(0);
    }

    @Override
    public List<Genre> getGenresList() {
        final String sqlQuery = "select * " +
                "from GENRES";
        return jdbcOperations.query(sqlQuery, new GenreRowMapper());
    }

    @Override
    public Genre add(Genre genre) {
        String sqlQuery = "insert into GENRES(NAME) " +
                "values (:name)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("name", genre.getName());
        jdbcOperations.update(sqlQuery, map, keyHolder);
        genre.setId(keyHolder.getKey().longValue());
        return genre;
    }

    @Override
    public void delete(long genreId) {
        String sqlQuery = "delete from GENRES " +
                "where GENRE_ID = :genreId";
        jdbcOperations.update(sqlQuery, Map.of("genreId", genreId));

        sqlQuery = "delete from FILMS_GENRES " +
                "where GENRE_ID = :genreId";
        jdbcOperations.update(sqlQuery, Map.of("genreId", genreId));
    }

    @Override
    public Genre update(Genre genre) {
        final String sqlQuery = "update GENRES " +
                "set NAME = :name " +
                "where GENRE_ID = " + genre.getId();
        jdbcOperations.update(sqlQuery, Map.of("name", genre.getName()));
        return getById(genre.getId());
    }
}