package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.rowMapper.DirectorRowMapper;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class DirectorDbRepository implements DirectorRepository {

    private final NamedParameterJdbcOperations jdbcTemplate;

    @Override
    public List<Director> getAllDirectors() {

        String sql = "select * from DIRECTORS";
        List<Director> directorList = jdbcTemplate.query(sql, new DirectorRowMapper());
        log.info("Найдено {} режиссеров", directorList.size());
        return directorList;
    }

    @Override
    public Director getDirectorById(int id) {
        return checkDirectorId(id);
    }

    @Override
    public Director addDirector(Director director) {

        String sql = "insert into DIRECTORS (NAME) VALUES (:name)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("name", director.getName());

        jdbcTemplate.update(sql, map, keyHolder);
        director.setId(keyHolder.getKey().intValue());
        log.info("Внесен новый режиссер {} c ID {}", director.getName(), director.getId());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        checkDirectorId(director.getId());
        String sql = "UPDATE DIRECTORS SET NAME = :name WHERE DIRECTOR_ID = :id";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("id", director.getId());
        map.addValue("name", director.getName());

        jdbcTemplate.update(sql, map);

        log.info("Режиссер ID {} изменен на {}", director.getId(), director.getName());
        return director;
    }

    @Override
    public void deleteDirector(int id) {
        checkDirectorId(id);
        String sql = "DELETE FROM FILM_DIRECTORS WHERE DIRECTOR_ID = :directorId";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("directorId", id);
        jdbcTemplate.update(sql, map);
        sql = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID = :directorId";
        jdbcTemplate.update(sql, map);
    }

    private Director checkDirectorId(int directorId) {
        String sql = "select * from DIRECTORS where DIRECTOR_ID = :directorId";
        List<Director> directors = jdbcTemplate.query(sql, Map.of("directorId", directorId), new DirectorRowMapper());
        if (directors.size() != 1) {
            throw new DirectorNotFoundException(directorId);
        }
        if (directors.get(0).getId() != directorId) {
            throw new DirectorNotFoundException(directorId);
        }
        return directors.get(0);
    }
}
