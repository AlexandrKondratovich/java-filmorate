package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validator.BirthdayConstraint;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    Long id;
    @NotNull
    @Email(message = "Некорректный email.")
    String email;
    @NotNull
    @NotBlank(message = "Логин не должен быть пустым.")
    String login;
    String name;
    @BirthdayConstraint
    LocalDate birthday;
}
