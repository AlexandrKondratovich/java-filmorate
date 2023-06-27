package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.model.FilmComparator;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
public class FilmController {
    public HashMap<Integer, Film> films = new HashMap<>();
    private int idCounter = 1;
    private ValidateService service = new ValidateService();

    @GetMapping("/films")
    public List<Film> getAll() {
        ArrayList<Film> list = new ArrayList<>(films.values());
        Collections.sort(list, new FilmComparator());
        return list;
    }

    @PostMapping(value = "/films")
    public Film add(@RequestBody @Valid Film film) {
        service.validateFilm(film);
        if (film.getId() != null && films.containsKey(film.getId())) {
            log.info("FilmAlreadyExistException: \"Фильм с таким ID уже есть в базе.\"");
            throw new FilmAlreadyExistException("Фильм с таким ID уже есть в базе.");
        }
        film.setId(idCounter++);
        films.put(film.getId(), film);
        log.debug("Фильм: {}", film);
        return film;
    }

    @PutMapping(value = "/films")
    public Film update(@RequestBody @Valid Film film) {
        service.validateFilm(film);
        if (!films.containsKey(film.getId())) {
            log.info("FilmNotFoundException: \"Фильма с таким ID нет в базе.\"");
            throw new FilmNotFoundException("Фильма с таким ID нет в базе.");
        }
        films.put(film.getId(), film);
        log.debug("Фильм: {}", film);
        return film;
    }
}
