package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ValidateService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Validated
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    final ValidateService validateService;
    final FilmService filmService;

    @GetMapping
    public List<Film> getAll() {
        return filmService.getAll();
    }

    @GetMapping("/{filmId}")
    public Film get(@PathVariable Integer filmId) {
        return filmService.get(filmId);
    }

    @DeleteMapping("/{filmId}")
    public void delete(@PathVariable Integer filmId) {
        filmService.delete(filmId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film add(@RequestBody @Valid Film film) {
        validateService.validateFilm(film);
        return filmService.add(film);
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film film) {
        validateService.validateFilm(film);
        filmService.update(film);
        return film;
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Film addLike(@PathVariable int filmId, @PathVariable int userId) {
        filmService.addLike(filmId, userId);
        return filmService.get(filmId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Film deleteLike(@PathVariable int filmId, @PathVariable int userId) {
        filmService.deleteLike(filmId, userId);
        return filmService.get(filmId);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(required = false) Integer count) {
        return filmService.getTopFilms(count);
    }
}
