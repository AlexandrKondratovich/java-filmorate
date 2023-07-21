package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaRepository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {

    @Qualifier("mpaDbRepository")
    final MpaRepository mpaRepository;

    public Mpa get(long mpaId) {
        return mpaRepository.getById(mpaId);
    }

    public List<Mpa> getAll() {
        return mpaRepository.getMpaList();
    }

    public Mpa add(Mpa mpa) {
        return mpaRepository.add(mpa);
    }

    public Mpa update(Mpa mpa) {
        return mpaRepository.update(mpa);
    }

    public void delete(long mpaId) {
        mpaRepository.delete(mpaId);
    }
}