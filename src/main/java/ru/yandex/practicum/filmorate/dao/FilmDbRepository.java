package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.rowMapper.DirectorRowMapper;
import ru.yandex.practicum.filmorate.dao.rowMapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.dao.rowMapper.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.*;


import javax.validation.ValidationException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

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
        checkDirectorsList(film);
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
        film.getDirectors()
                .forEach(director -> addDirectorToFilm(film.getId(), director.getId()));
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

        sqlQuery = "delete from FILM_DIRECTORS " +
                "where FILM_ID = :filmId";
        jdbcOperations.update(sqlQuery, Map.of("filmId", filmId));
    }

    @Override
    public Film update(Film film) {
        checkFilmId(film.getId());
        checkDirectorsList(film);
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

        updateGenres(film);
        return updateDirectors(film);
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
        return jdbcOperations.query(sqlQuery, Map.of("filmId", filmId), new UserRowMapper());
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
            jdbcOperations.update(sqlQuery, Map.of("filmId", filmId, "userId", userId));
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

    @Override
    public List<Genre> getFilmGenres(long filmId) {
        final String sqlQuery = "select * " +
                "from GENRES " +
                "where GENRE_ID in (" +
                "select GENRE_ID " +
                "from FILMS_GENRES " +
                "where FILM_ID = :filmId) " +
                "order by GENRE_ID";
        return jdbcOperations.query(sqlQuery, Map.of("filmId", filmId), new GenreRowMapper());
    }

    @Override
    public List<Film> getMostPopularFilmsByYearAndGenre(long genreId, int year, int count) {
        final String sqlQuery = "select F.FILM_ID, " +
                                       "F.NAME, " +
                                       "F.RELEASE_DATE, " +
                                       "F.DESCRIPTION, " +
                                       "F.DURATION, " +
                                       "M.MPA_ID, " +
                                       "M.NAME, " +
                                       "COUNT(L.USER_ID) " +
                "from FILMS as F " +
                "join MPA as M on F.MPA_ID = M.MPA_ID " +
                "left join LIKES L on F.FILM_ID = L.FILM_ID " +
                "where F.FILM_ID IN (" +
                    "select FILM_ID " +
                    "from FILMS_GENRES " +
                    "where GENRE_ID = :genreId) " +
                "and extract(YEAR from F.RELEASE_DATE) = :year " +
                "group by F.FILM_ID " +
                "order by COUNT(L.USER_ID) " +
                "limit :count";
        return jdbcOperations.query(sqlQuery,
                Map.of("genreId", genreId, "year", year, "count", count),
                new FilmRowMapper());
    }

    @Override
    public List<Film> getMostPopularFilmsByYear(int year, int count) {
        final String sqlQuery = "select F.FILM_ID, " +
                                       "F.NAME, " +
                                       "F.RELEASE_DATE, " +
                                       "F.DESCRIPTION, " +
                                       "F.DURATION, " +
                                       "M.MPA_ID, " +
                                       "M.NAME, " +
                                       "COUNT(L.USER_ID) " +
                "from FILMS as F " +
                "join MPA as M on F.MPA_ID = M.MPA_ID " +
                "left join LIKES L on F.FILM_ID = L.FILM_ID " +
                "where extract(YEAR from F.RELEASE_DATE) = :year " +
                "group by F.FILM_ID " +
                "order by COUNT(L.USER_ID) " +
                "limit :count";
        return jdbcOperations.query(sqlQuery, Map.of("year", year, "count", count), new FilmRowMapper());
    }

    @Override
    public List<Film> getMostPopularFilmsByGenre(long genreId, int count) {
        final String sqlQuery = "select F.FILM_ID, " +
                                       "F.NAME, " +
                                       "F.RELEASE_DATE, " +
                                       "F.DESCRIPTION, " +
                                       "F.DURATION, " +
                                       "M.MPA_ID, " +
                                       "M.NAME, " +
                                       "COUNT(L.USER_ID) " +
                "from FILMS as F " +
                "join MPA as M on F.MPA_ID = M.MPA_ID " +
                "left join LIKES L on F.FILM_ID = L.FILM_ID " +
                "where F.FILM_ID IN (" +
                "select FILM_ID " +
                "from FILMS_GENRES " +
                "where GENRE_ID = :genreId) " +
                "group by F.FILM_ID " +
                "order by COUNT(L.USER_ID) " +
                "limit :count";
        return jdbcOperations.query(sqlQuery, Map.of("genreId", genreId,"count", count), new FilmRowMapper());
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        final String sqlQuery = "select F.FILM_ID, " +
                                       "F.NAME, " +
                                       "F.RELEASE_DATE, " +
                                       "F.DESCRIPTION, " +
                                       "F.DURATION, " +
                                       "M.MPA_ID, " +
                                       "M.NAME, " +
                                       "COUNT(L.USER_ID) " +
                "from FILMS as F " +
                "join MPA as M on F.MPA_ID = M.MPA_ID " +
                "left join LIKES L on F.FILM_ID = L.FILM_ID " +
                "group by F.FILM_ID " +
                "order by COUNT(L.USER_ID) DESC " +
                "limit :count";
        return jdbcOperations.query(sqlQuery,  Map.of("count", count), new FilmRowMapper());
    }

    @Override
    public List<Film> getDirectorFilmListByYear(int directorId) {
        checkDirectorById(directorId);

        final String sqlQuery = "select * " +
                "from FILMS as F " +
                "join MPA as M ON F.MPA_ID=M.MPA_ID " +
                "join FILM_DIRECTORS FD on F.FILM_ID = FD.FILM_ID " +
                "join DIRECTORS D on FD.DIRECTOR_ID = D.DIRECTOR_ID " +
                "where D.DIRECTOR_ID = :directorId " +
                "order by RELEASE_DATE";

        return jdbcOperations.query(sqlQuery, Map.of("directorId", directorId), new FilmRowMapper());
    }

    @Override
    public List<Film> getDirectorFilmListByLikes(int directorId) {
        checkDirectorById(directorId);

        final String sqlQuery = "select F.FILM_ID, F.NAME, DESCRIPTION, RELEASE_DATE, DURATION, M.MPA_ID, M.NAME " +
                "from FILMS as F " +
                "join MPA as M ON F.MPA_ID=M.MPA_ID " +
                "join FILM_DIRECTORS FD on F.FILM_ID = FD.FILM_ID " +
                "left join LIKES L on F.FILM_ID = L.FILM_ID " +
                "where FD.DIRECTOR_ID = :directorId " +
                "group by F.FILM_ID " +
                "order by COUNT(L.USER_ID) DESC";

        return jdbcOperations.query(sqlQuery, Map.of("directorId", directorId), new FilmRowMapper());
    }

    @Override
    public List<Film> searchFilmsByDirAndName(String query) {
        String regex = "%" + query + "%";
        String sql = "SELECT F.FILM_ID, F.NAME, DESCRIPTION, RELEASE_DATE, DURATION, M.MPA_ID, M.NAME " +
                "from FILMS as F " +
                "join MPA as M ON F.MPA_ID=M.MPA_ID " +
                "left join LIKES L on F.FILM_ID = L.FILM_ID " +
                "where UPPER(F.NAME) like UPPER(:regex) OR F.FILM_ID IN (" +
                "SELECT FD.FILM_ID " +
                "FROM FILM_DIRECTORS FD " +
                "LEFT JOIN DIRECTORS D on D.DIRECTOR_ID = FD.DIRECTOR_ID " +
                "WHERE UPPER(D.NAME) LIKE UPPER(:regex)" +
                ") " +
                "group by F.FILM_ID " +
                "order by COUNT(L.USER_ID) DESC";

        return jdbcOperations.query(sql, Map.of("regex", regex), new FilmRowMapper());
    }

    @Override
    public List<Film> searchFilmsByName(String query) {
        String regex = "%" + query + "%";
        String sql = "SELECT F.FILM_ID, F.NAME, DESCRIPTION, RELEASE_DATE, DURATION, M.MPA_ID, M.NAME " +
                "FROM FILMS AS F " +
                "JOIN MPA AS M ON F.MPA_ID = M.MPA_ID " +
                "LEFT JOIN LIKES AS L ON F.FILM_ID = L.FILM_ID " +
                "WHERE UPPER(F.NAME) LIKE UPPER(:regex) " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY COUNT(L.USER_ID) DESC";

        return jdbcOperations.query(sql, Map.of("regex", regex), new FilmRowMapper());
    }

    @Override
    public List<Film> searchFilmsByDir(String query) {
        String regex = "%" + query + "%";
        String sql = "SELECT F.FILM_ID, F.NAME, DESCRIPTION, RELEASE_DATE, DURATION, M.MPA_ID, M.NAME " +
                "FROM FILMS AS F " +
                "join MPA as M ON F.MPA_ID=M.MPA_ID " +
                "left join LIKES L on F.FILM_ID = L.FILM_ID " +
                "where F.FILM_ID IN (" +
                "SELECT FD.FILM_ID " +
                "FROM FILM_DIRECTORS FD " +
                "LEFT JOIN DIRECTORS D on D.DIRECTOR_ID = FD.DIRECTOR_ID " +
                "WHERE UPPER(D.NAME) LIKE UPPER(:regex)" +
                ") " +
                "group by F.FILM_ID " +
                "order by COUNT(L.USER_ID) DESC";

        return jdbcOperations.query(sql, Map.of("regex", regex), new FilmRowMapper());
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

    private void checkDirectorById(int id) {
        String sql = "select * from DIRECTORS where DIRECTOR_ID = :id";
        List<Director> directorList = jdbcOperations.query(sql, Map.of("id", id), new DirectorRowMapper());
        if (directorList.isEmpty()) {
            throw new DirectorNotFoundException("Режиссера c идентификатором " + id + " не найдено в базе ");
        }
    }

    private List<Director> getDirectorListByFilmId(Long filmId) {
        String sql = "select FD.DIRECTOR_ID, D.NAME " +
                "FROM FILM_DIRECTORS as FD " +
                "JOIN DIRECTORS AS D ON FD.DIRECTOR_ID = D.DIRECTOR_ID " +
                "where FD.FILM_ID = :filmId";

        return jdbcOperations.query(sql, Map.of("filmId", filmId), new DirectorRowMapper());
    }

    public void deleteLikesByFilmId(long filmId) {
        checkFilmId(filmId);
        final String sqlQuery = "delete from LIKES " +
                "where FILM_ID = :filmId";
        jdbcOperations.update(sqlQuery, Map.of("filmId", filmId));
    }

    private void updateGenres(Film film) {
        final String deleteSqlQuery = "delete from FILMS_GENRES " +
                "where FILM_ID = :filmId";
        jdbcOperations.update(deleteSqlQuery, Map.of("filmId", film.getId()));
        final String addSqlQuery = "insert into FILMS_GENRES (FILM_ID, GENRE_ID) " +
                "values (:filmId, :genreId)";
        film.getGenres().forEach(genre ->
                jdbcOperations.update(addSqlQuery, Map.of("filmId", film.getId(), "genreId", genre.getId())));
    }

    private Film updateDirectors(Film film) {
        final String deleteSqlQuery = "delete from FILM_DIRECTORS " +
                "where FILM_ID = :filmId";
        jdbcOperations.update(deleteSqlQuery, Map.of("filmId", film.getId()));

        film.getDirectors()
                .forEach(director -> addDirectorToFilm(film.getId(), director.getId()));
        return getById(film.getId());
    }

    private void addDirectorToFilm(long filmId, int directorId) {

        String sql = "INSERT INTO FILM_DIRECTORS(FILM_ID, DIRECTOR_ID) VALUES ( :filmId, :directorId )";
        jdbcOperations.update(sql, Map.of("filmId", filmId, "directorId", directorId));
    }

    private void checkDirectorsList(Film film) {

        String sql = "select * from DIRECTORS";

        if (!film.getDirectors().isEmpty()) {
            List<Integer> filmDirectorIdList = film.getDirectors().stream()
                    .map(Director::getId)
                    .distinct()
                    .collect(Collectors.toList());
            List<Integer> directorsInDb = jdbcOperations.query(sql, new DirectorRowMapper()).stream()
                    .map(Director::getId)
                    .collect(Collectors.toList());
            for (Integer directorId : filmDirectorIdList) {
                if (!directorsInDb.contains(directorId)) {
                    throw new ValidationException("Режиссер в запросе должен соответствовать с базой данных");
                }
            }
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
                    new HashSet<>(getDirectorListByFilmId(rs.getLong("FILM_ID"))),
                    new HashSet<>(getFilmGenres(rs.getLong("FILM_ID"))),
                    new HashSet<>(getFilmLikes(rs.getLong("FILM_ID"))));
        }
    }

}