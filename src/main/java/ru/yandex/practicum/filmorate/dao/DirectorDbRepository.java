package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.rowMapper.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public Optional<Director> getDirectorById(int id) {

        String sql = "select * from DIRECTORS where DIRECTOR_ID = :id";
        List<Director> directorList = jdbcTemplate.query(sql, Map.of("id", id), new DirectorRowMapper());
        if (!directorList.isEmpty()) {
            log.info("Найден режиссер с ID: {} и именем {} ", directorList.get(0).getId(), directorList.get(0).getName());
            return Optional.of(directorList.get(0));
        } else {
            log.info("Режиссера c идентификатором {} не найдено", id);
            return Optional.empty();
        }
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

        String sql = "UPDATE DIRECTORS SET NAME = :name WHERE DIRECTOR_ID = :id";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("id", director.getId());
        map.addValue("name", director.getName());

        jdbcTemplate.update(sql, map);

        log.info("Режиссер ID {} изменен на {}", director.getId(), director.getName());
        return director;
    }

    @Override
    public boolean deleteDirector(int id) {

        String sql = "DELETE FROM FILM_DIRECTORS WHERE DIRECTOR_ID = :directorId";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("directorId", id);
        jdbcTemplate.update(sql, map);

        sql = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID = :directorId";
        MapSqlParameterSource map1 = new MapSqlParameterSource();
        map1.addValue("directorId", id);

        int count = jdbcTemplate.update(sql, map1);
        if (count > 0) {
            log.info("Удален режиссер с ID {}", id);
            return true;
        } else {
            log.info("Режиссер с ID {} нет в базе, удалять нечего", id);
            return false;
        }
    }
}
