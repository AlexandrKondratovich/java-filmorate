package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.rowMapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.dao.rowMapper.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class FilmDbRepository implements FilmRepository {

    private final NamedParameterJdbcOperations jdbcOperations;

    public FilmDbRepository(NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }


    @Override
    public Film getById(long filmId) {
        checkFilmId(filmId);
        final String sqlQuery = "select * " +
                "from FILMS as F " +
                "join MPA as M ON F.MPA_ID=M.MPA_ID " +
                "where F.FILM_ID = :filmId ";
        final List<Film> films = jdbcOperations.query(sqlQuery, Map.of("filmId", filmId), new FilmRowMapper());
        return films.get(0);
    }

    @Override
    public List<Film> getFilmsList() {
        final String sqlQuery = "select * " +
                "from FILMS as F " +
                "join MPA as M ON F.MPA_ID=M.MPA_ID " +
                "order by FILM_ID";
        return jdbcOperations.query(sqlQuery, new FilmRowMapper());
    }

    @Override
    public Film add(Film film) {
        String sqlQuery = "insert into FILMS(NAME, RELEASE_DATE, DESCRIPTION, MPA_ID, DURATION) " +
                "values (:name, :releaseDate, :description, :mpaId, :duration)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("name", film.getName());
        map.addValue("releaseDate", film.getReleaseDate());
        map.addValue("description", film.getDescription());
        map.addValue("mpaId", film.getMpa().getId());
        map.addValue("duration", film.getDuration());
        jdbcOperations.update(sqlQuery, map, keyHolder);
        film.setId(keyHolder.getKey().longValue());
        film.getLikes()
                .forEach(user -> addLike(film.getId(), user.getId()));
        film.getGenres()
                .forEach(genre -> addGenreToFilm(film.getId(), genre.getId()));
        return getById(film.getId());
    }

    @Override
    public void delete(long filmId) {
        checkFilmId(filmId);
        deleteLikesByFilmId(filmId);
        String sqlQuery = "delete from FILMS_GENRES " +
                "where FILM_ID = :filmId";
        jdbcOperations.update(sqlQuery, Map.of("filmId", filmId));

        sqlQuery = "delete from FILMS " +
                "where FILM_ID = :filmId";
        jdbcOperations.update(sqlQuery, Map.of("filmId", filmId));
    }

    @Override
    public Film update(Film film) {
        checkFilmId(film.getId());
        String sqlQuery = "update FILMS " +
                "set NAME = :name, " +
                "RELEASE_DATE = :releaseDate, " +
                "DESCRIPTION = :description, " +
                "MPA_ID = :mpaId, " +
                "DURATION = :duration " +
                "where FILM_ID = :filmId";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("filmId", film.getId());
        map.addValue("name", film.getName());
        map.addValue("releaseDate", film.getReleaseDate());
        map.addValue("description", film.getDescription());
        map.addValue("mpaId", film.getMpa().getId());
        map.addValue("duration", film.getDuration());
        jdbcOperations.update(sqlQuery, map);

        deleteLikesByFilmId(film.getId());
        film.getLikes().stream()
                .map(User::getId)
                .forEach(userId -> addLike(film.getId(), userId));

        updateGenres(film);
        return getById(film.getId());
    }

    private void updateGenres(Film film) {
        final String deleteSqlQuery = "delete from FILMS_GENRES " +
                "where FILM_ID = :filmId";
        jdbcOperations.update(deleteSqlQuery, Map.of("filmId", film.getId()));
        final String addSqlQuery = "insert into FILMS_GENRES (FILM_ID, GENRE_ID) " +
                "values (:filmId, :genreId)";
        film.getGenres().forEach(genre ->
                jdbcOperations.update(addSqlQuery, Map.of("filmId", film.getId(),"genreId", genre.getId())));
    }

    @Override
    public List<User> getFilmLikes(long filmId) {
        checkFilmId(filmId);
        final String sqlQuery = "select * " +
                "from USERS " +
                "where USER_ID in (" +
                                "select USER_ID " +
                                "from LIKES " +
                                "where FILM_ID = :filmId) " +
                "order by USER_ID";
        return jdbcOperations.query(sqlQuery,Map.of("filmId", filmId), new UserRowMapper());
    }

    @Override
    public void addLike(long filmId, long userId) {
        checkFilmId(filmId);
        checkUserId(userId);
        String sqlQuery = "select USER_ID " +
                "from LIKES " +
                "where FILM_ID = :filmId";
        if (!(jdbcOperations.queryForList(sqlQuery, Map.of("filmId", filmId), Long.class).contains(userId))) {
            sqlQuery = "insert into LIKES (FILM_ID, USER_ID) " +
                    "values (:filmId, :userId)";
            jdbcOperations.update(sqlQuery,Map.of("filmId", filmId, "userId", userId));
        }
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        checkFilmId(filmId);
        checkUserId(userId);
        final String sqlQuery = "delete from LIKES " +
                "where FILM_ID = :filmId AND USER_ID = :userId";
        jdbcOperations.update(sqlQuery, Map.of("filmId", filmId, "userId", userId));
    }

    public void deleteLikesByFilmId(long filmId) {
        checkFilmId(filmId);
        final String sqlQuery = "delete from LIKES " +
                "where FILM_ID = :filmId";
        jdbcOperations.update(sqlQuery,Map.of("filmId", filmId));
    }

    @Override
    public List<Genre> getFilmGenres(long filmId) {
        final String sqlQuery = "select * " +
                "from GENRES " +
                "where GENRE_ID in (" +
                                    "select GENRE_ID " +
                                    "from FILMS_GENRES " +
                                    "where FILM_ID = :filmId) " +
                "order by GENRE_ID";
        return jdbcOperations.query(sqlQuery,Map.of("filmId", filmId), new GenreRowMapper());
    }

    private void addGenreToFilm(long filmId, long genreId) {
        String sqlQuery = "select GENRE_ID " +
                "from FILMS_GENRES " +
                "where FILM_ID = :filmId";
        List<Long> genres = jdbcOperations.queryForList(sqlQuery, Map.of("filmId", filmId), Long.class);
        sqlQuery = "insert into FILMS_GENRES(FILM_ID, GENRE_ID) " +
                "values (:filmId, :genreId)";
        if (!genres.contains(genreId)) {
            jdbcOperations.update(sqlQuery, Map.of("filmId", filmId, "genreId", genreId));
        }
    }



    private void checkUserId(long userId) {
        final String sqlQuery = "select USER_ID " +
                "from USERS " +
                "where USER_ID = :userId ";
        List<Long> usersId = jdbcOperations.queryForList(sqlQuery, Map.of("userId", userId), Long.class);
        if (usersId.size() != 1) {
            throw new UserNotFoundException(userId);
        }
    }

    private void checkFilmId(long filmId) {
        String sqlQuery = "select FILM_ID " +
                "from FILMS " +
                "where FILM_ID = :filmId";
        List<Long> ids = jdbcOperations.queryForList(sqlQuery, Map.of("filmId", filmId), Long.class);
        if (!ids.contains(filmId)) {
            throw new FilmNotFoundException(filmId);
        }
    }

    private class FilmRowMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Film(rs.getLong("FILM_ID"),
                            rs.getString("NAME"),
                            rs.getString("DESCRIPTION"),
                            rs.getDate("RELEASE_DATE").toLocalDate(),
                            rs.getLong("DURATION"),
                            new Mpa(rs.getLong("MPA.MPA_ID"), rs.getString("MPA.NAME")),
                            new HashSet<>(getFilmGenres(rs.getLong("FILM_ID"))),
                            new HashSet<>(getFilmLikes(rs.getLong("FILM_ID"))));
        }
    }

}