package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
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
    public Film get(@PathVariable Long filmId) {
        return filmService.get(filmId);
    }

    @DeleteMapping("/{filmId}")
    public void delete(@PathVariable Long filmId) {
        filmService.delete(filmId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film add(@RequestBody @Valid Film film) {
        validateService.validateFilm(film);
        film.getDirectors().forEach(director -> validateService.validDirectorId(director.getId()));
        return filmService.add(film);
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film film) {
        validateService.validateFilm(film);
        film.getDirectors().forEach(director -> validateService.validDirectorId(director.getId()));
        return filmService.update(film);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Film addLike(@PathVariable long filmId, @PathVariable long userId) {
        filmService.addLike(filmId, userId);
        return filmService.get(filmId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Film deleteLike(@PathVariable long filmId, @PathVariable long userId) {
        filmService.deleteLike(filmId, userId);
        return filmService.get(filmId);
    }

    @GetMapping("/{filmId}/likes")
    public List<User> getFIlmLikes(@PathVariable long filmId) {
        return filmService.getFIlmLikes(filmId);
    }

    @GetMapping("/{filmId}/genres")
    public List<Genre> getFilmGenres(@PathVariable long filmId) {
        return filmService.getFilmGenres(filmId);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(required = false) Integer count) {
        return filmService.getTopFilms(count);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getDirectorFilmListByYearOrLike(@PathVariable int directorId, @RequestParam String sortBy) {
        validateService.validDirectorId(directorId);
        List<Film> filmList;
        if (sortBy.equals("year")) {
            filmList = filmService.getDirectorFilmListByYear(directorId);
        } else if (sortBy.equals("likes")) {
            filmList = filmService.getDirectorFilmListByLikes(directorId);
        } else {
            throw new IncorrectParameterException("В запрос передан неправильный параметр, нужен 'like' или 'year'");
        }
        return filmList;
    }
}