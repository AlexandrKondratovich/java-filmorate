package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validator.ReleaseDateConstraint;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class Film {
    Integer id;
    @NotBlank(message = "Название фильма не может быть пустым.")
    String name;
    @Size(max = 200, message = "Описание должно содержать не более 200 символов.")
    String description;
    @ReleaseDateConstraint
    LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной.")
    Long duration;
}
