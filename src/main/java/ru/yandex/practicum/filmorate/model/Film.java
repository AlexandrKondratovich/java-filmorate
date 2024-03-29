package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.validator.ReleaseDateConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    Long id;
    @NotNull
    @NotBlank(message = "Название фильма не может быть пустым.")
    String name;
    @Size(max = 200, message = "Описание должно содержать не более 200 символов.")
    String description;
    @ReleaseDateConstraint
    LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной.")
    Long duration;
    Mpa mpa;
    Set<Genre> genres = new HashSet<>();
    Set<User> likes = new HashSet<>();
}