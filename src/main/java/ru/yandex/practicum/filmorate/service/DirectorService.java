package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import javax.validation.ValidationException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorStorage directorStorage;

    public List<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    public Director getDirectorById(int id) {
        return directorStorage.getDirectorById(id).orElseThrow(() ->
                new DirectorNotFoundException("Режиссера с ID " + id + " нет в базе"));
    }

    public Director addDirector(Director director) {
        if (StringUtils.isBlank(director.getName())) {
            throw new ValidationException("Имя режиссера обзятельно для заполнения");
        }
        return directorStorage.addDirector(director);
    }

    public Director updateDirector(Director director) {
        directorStorage.getDirectorById(director.getId()).orElseThrow(() ->
                new DirectorNotFoundException("Режиссера с ID " + director.getId() + " нет в базе"));
        if (StringUtils.isBlank(director.getName())) {
            throw new ValidationException("Имя режиссера обзятельно для заполнения");
        }
        return directorStorage.updateDirector(director);
    }

    public boolean deleteDirector(int id) {
        directorStorage.getDirectorById(id).orElseThrow(() ->
                new DirectorNotFoundException("Режиссера с ID " + id + " нет в базе"));
        return directorStorage.deleteDirector(id);
    }

}
