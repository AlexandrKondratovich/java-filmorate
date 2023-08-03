package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Review {
    private long reviewId;
    @NotNull
    @Size(min = 10, max = 200)
    private String content;
    @NotNull
    private boolean isPositive; // если положительный тогда рейтинг +1
    @NotNull
    private long userId;
    @NotNull
    private long filmId;
    private int useful = 0; //если true, тогда rating + 1 иначе rating - 1
}
