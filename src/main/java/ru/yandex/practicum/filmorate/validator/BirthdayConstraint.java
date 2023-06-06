package ru.yandex.practicum.filmorate.validator;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BirthdayValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface BirthdayConstraint {
    String message() default "Дата рождения не должна быть позже сегодняшнего числа.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}