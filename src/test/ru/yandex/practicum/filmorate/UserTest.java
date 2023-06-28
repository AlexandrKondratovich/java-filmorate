package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.BirthdayConstraint;

import javax.validation.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private static Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    void shouldCreateCorrectUser() {
        User user = new User();
        user.setLogin("login");
        user.setName("name");
        user.setEmail("email@mail.ru");
        user.setBirthday(LocalDate.of(1998, 11, 29));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Присутствуют нарушения.");
        assertNull(user.getId(), "Был присвоен ID.");
        assertEquals(user.getLogin(), "login", "Логин был присвоен некорректно.");
        assertEquals(user.getName(), "name", "Имя было присвоено некорректно.");
        assertEquals(user.getBirthday(), LocalDate.of(1998, 11, 29),
                "Дата рождения была присвоена некорректно.");
    }

    @Test
    void shouldValidateEmptyLogin() {
        User user = new User();
        user.setLogin(" ");
        user.setEmail("email@mail.ru");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Нарушения отсутствуют.");
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals(NotBlank.class, violation.getConstraintDescriptor().getAnnotation().annotationType(),
                "Нарушение \"NotBlank\" не найдено.");
        assertEquals("login", violation.getPropertyPath().toString(),
                "Не найдено нарушение для поля \"login\".");
    }

    @Test
    void shouldValidateIncorrectEmail() {
        User user = new User();
        user.setLogin("login");
        user.setEmail("email");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Нарушения отсутствуют.");
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals(Email.class, violation.getConstraintDescriptor().getAnnotation().annotationType(),
                "Нарушение \"Email\" не найдено.");
        assertEquals("email", violation.getPropertyPath().toString(),
                "Не найдено нарушение для поля \"email\".");
    }

    @Test
    void shouldValidateFutureBirthday() {
        User user = new User();
        user.setLogin("login");
        user.setEmail("email@mail.ru");
        user.setBirthday(LocalDate.of(2024,1,1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Нарушения отсутствуют.");
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals(BirthdayConstraint.class, violation.getConstraintDescriptor().getAnnotation().annotationType(),
                "Нарушение \"ReleaseDateConstraint\" не найдено.");
        assertEquals("birthday", violation.getPropertyPath().toString(),
                "Не найдено нарушение для поля \"birthday\".");
    }

    @Test
    void shouldCreateUserWithTodayBirthday() {
        User user = new User();
        user.setLogin("login");
        user.setEmail("email@mail.ru");
        user.setBirthday(LocalDate.now());

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Нарушения присутствуют.");
    }
}
