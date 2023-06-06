package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.ReleaseDateConstraint;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmTest {

    private static Validator validator;
    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    void shouldCreateCorrectFilm(){
        Film film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(2030,1,10));
        film.setDuration(100L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Присутствуют нарушения.");
        assertNull(film.getId(), "Был присвоен ID.");
        assertEquals(film.getDescription(), "description", "Описание было присвоено некорректно.");
        assertEquals(film.getName(), "name", "Имя было присвоено некорректно.");
        assertEquals(film.getReleaseDate(), LocalDate.of(2030,1,10),
                "Дата релиза была присвоена некорректно.");
    }

    @Test
    void shouldValidateEmptyName(){
        Film film = new Film();
        film.setName(" ");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Нарушения отсутствуют.");
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals(NotBlank.class, violation.getConstraintDescriptor().getAnnotation().annotationType(),
                "Нарушение \"NotBlank\" не найдено.");
        assertEquals("name", violation.getPropertyPath().toString(),
                "Не найдено нарушение для поля \"name\".");
    }

    @Test
    void shouldValidateLongDescription(){
        Film film = new Film();
        film.setName("name");
        film.setDescription(".SKBFzkjshbfbzbfzhbfkbfkbjhfbhjzbhbHBKVkgGVhgVKHGVHGJvKVkhGCVcGH" +
                ".SKBFzkjshbfbzbfzhbfkbfkbjhfbhjzbhbHBKVkgGVhgVKHGVHGJvKVkhGCVcGH" +
                ".SKBFzkjshbfbzbfzhbfkbfkbjhfbhjzbhbHBKVkgGVhgVKHGVHGJvKVkhGCVcGH" +
                ".SKBFzkjshbfbzbfzhbfkbfkbjhfbhjzbhbHBKVkgGVhgVKHGVHGJvKVkhGCVcGH");
        //256 символов
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Нарушения отсутствуют.");
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals(Size.class, violation.getConstraintDescriptor().getAnnotation().annotationType(),
                "Нарушение \"Size\" не найдено.");
        assertEquals("description", violation.getPropertyPath().toString(),
                "Не найдено нарушение для поля \"description\".");
    }

    @Test
    void shouldCreate200SymbolsSizeDescription(){
        Film film = new Film();
        film.setName("name");
        film.setDescription(".SKBFzkjshbfbzbfzhbfkbfkbjhfbhjzbhbHBKVkgGVhgVKHGVHGJvKVkhGCVcGH" +
                ".SKBFzkjshbfbzbfzhbfkbfkbjhfbhjzbhbHBKVkgGVhgVKHGVHGJvKVkhGCVcGH" +
                ".SKBFzkjshbfbzbfzhbfkbfkbjhfbhjzbhbHBKVkgGVhgVKHGVHGJvKVkhGCVcGH" +
                "khGCVcGH");
        //200 символов
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Нарушения присутствуют.");
    }

    @Test
    void shouldValidateIncorrectReleaseDate(){
        Film film = new Film();
        film.setName("name");
        film.setReleaseDate(LocalDate.of(1800,1,1));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Нарушения отсутствуют.");
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals(ReleaseDateConstraint.class, violation.getConstraintDescriptor().getAnnotation().annotationType(),
                "Нарушение \"ReleaseDateConstraint\" не найдено.");
        assertEquals("releaseDate", violation.getPropertyPath().toString(),
                "Не найдено нарушение для поля \"releaseDate\".");
    }

    @Test
    void shouldCreateFilmWith28December1895ReleaseDate(){
        Film film = new Film();
        film.setName("name");
        film.setReleaseDate(LocalDate.of(1895,12,28));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Нарушения присутствуют.");
    }

    @Test
    void shouldValidateZeroDuration(){
        Film film = new Film();
        film.setName("name");
        film.setDuration(0L);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Нарушения отсутствуют.");
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals(Positive.class, violation.getConstraintDescriptor().getAnnotation().annotationType(),
                "Нарушение \"Positive\" не найдено.");
        assertEquals("duration", violation.getPropertyPath().toString(),
                "Не найдено нарушение для поля \"duration\".");
    }

    @Test
    void shouldValidateNegativeDuration(){
        Film film = new Film();
        film.setName("name");
        film.setDuration(-10L);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Нарушения отсутствуют.");
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals(Positive.class, violation.getConstraintDescriptor().getAnnotation().annotationType(),
                "Нарушение \"Positive\" не найдено.");
        assertEquals("duration", violation.getPropertyPath().toString(),
                "Не найдено нарушение для поля \"duration\".");
    }
}
