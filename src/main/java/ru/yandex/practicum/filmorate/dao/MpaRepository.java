package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaRepository {

    Mpa getById(long mpaId);

    List<Mpa> getMpaList();

    Mpa add(Mpa mpa);

    void delete(long mpaId);

    Mpa update(Mpa mpa);
}
