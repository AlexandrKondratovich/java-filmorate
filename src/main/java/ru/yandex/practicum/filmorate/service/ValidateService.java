package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.*;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class ValidateService {

    private static final Validator validator;
    private final DirectorService directorService;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    public void validateUser(User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations.isEmpty()) {
            return;
        }
        ConstraintViolation<User> violation = violations.iterator().next();
        if (violation.getPropertyPath().toString().equals("email")) {
            log.warn("Некорректный email: {}", user);
            throw new ValidationException("Некорректный email: " + violation.getMessage());
        }
        if (violation.getPropertyPath().toString().equals("login")) {
            log.warn("Некорректный логин: {}", user);
            throw new ValidationException("Некорректный логин: " + violation.getMessage());
        }
        if (violation.getPropertyPath().toString().equals("birthday")) {
            log.warn("Некорректная дата рождения: {}", user);
            throw new ValidationException("Некорректная дата рождения: " + violation.getMessage());
        }
    }

    public void validateFilm(Film film) {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (violations.isEmpty()) {
            return;
        }
        ConstraintViolation<Film> violation = violations.iterator().next();
        if (violation.getPropertyPath().toString().equals("name")) {
            log.warn("Некорректное название: {}", film);
            throw new ValidationException("Некорректное название: " + violation.getMessage());
        }
        if (violation.getPropertyPath().toString().equals("size")) {
            log.warn("Некорректное описание: {}", film);
            throw new ValidationException("Некорректное описание: " + violation.getMessage());
        }
        if (violation.getPropertyPath().toString().equals("releaseDate")) {
            log.warn("Некорректная дата релиза: {}", film);
            throw new ValidationException("Некорректная дата релиза: " + violation.getMessage());
        }
        if (violation.getPropertyPath().toString().equals("duration")) {
            log.warn("Некорректная длительность: {}", film);
            throw new ValidationException("Некорректная длительность: " + violation.getMessage());
        }
    }
}