package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/genres")
@Validated
@Slf4j
@RequiredArgsConstructor
public class GenreController {
    final GenreService genreService;

    @GetMapping
    public List<Genre> getAll() {
        return genreService.getAll();
    }

    @GetMapping("/{genreId}")
    public Genre get(@PathVariable Long genreId) {
        return genreService.get(genreId);
    }

    @DeleteMapping("/{genreId}")
    public void delete(@PathVariable Long genreId) {
        genreService.delete(genreId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Genre add(@RequestBody @Valid Genre genre) {
        return genreService.add(genre);
    }

    @PutMapping
    public Genre update(@RequestBody @Valid Genre genre) {
        return genreService.update(genre);
    }
}
