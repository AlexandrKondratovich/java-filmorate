package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/mpa")
@Validated
@Slf4j
@RequiredArgsConstructor
public class MpaController {
    final MpaService mpaService;

    @GetMapping
    public List<Mpa> getAll() {
        return mpaService.getAll();
    }

    @GetMapping("/{mpaId}")
    public Mpa get(@PathVariable Long mpaId) {
        return mpaService.get(mpaId);
    }

    @DeleteMapping("/{mpaId}")
    public void delete(@PathVariable Long mpaId) {
        mpaService.delete(mpaId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mpa add(@RequestBody @Valid Mpa mpa) {
        return mpaService.add(mpa);
    }

    @PutMapping
    public Mpa update(@RequestBody @Valid Mpa mpa) {
        return mpaService.update(mpa);
    }
}