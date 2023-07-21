package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.rowMapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Map;

@Component
public class MpaDbRepository implements MpaRepository {

    private final NamedParameterJdbcOperations jdbcOperations;

    public MpaDbRepository(NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public Mpa getById(long mpaId) {
        final String sqlQuery = "select * " +
                "from MPA " +
                "where MPA_ID = :mpaId ";
        final List<Mpa> mpas = jdbcOperations.query(sqlQuery, Map.of("mpaId", mpaId), new MpaRowMapper());
        if (mpas.size() != 1) {
            throw new MpaNotFoundException(mpaId);
        }
        return mpas.get(0);
    }

    @Override
    public List<Mpa> getMpaList() {
        final String sqlQuery = "select * " +
                "from MPA";
        return jdbcOperations.query(sqlQuery, new MpaRowMapper());
    }

    @Override
    public Mpa add(Mpa mpa) {
        String sqlQuery = "insert into MPA(NAME) " +
                "values (:name)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("name", mpa.getName());
        jdbcOperations.update(sqlQuery, map, keyHolder);
        return getById(keyHolder.getKey().longValue());
    }

    @Override
    public void delete(long mpaId) {
        String sqlQuery = "delete from MPA " +
                "where MPA_ID = :mpaId";
        jdbcOperations.update(sqlQuery, Map.of("mpaId", mpaId));
    }

    @Override
    public Mpa update(Mpa mpa) {
        final String sqlQuery = "update MPA " +
                "set NAME = :name " +
                "where MPA_ID = " + mpa.getId();
        jdbcOperations.update(sqlQuery, Map.of("name", mpa.getName()));
        return getById(mpa.getId());
    }
}