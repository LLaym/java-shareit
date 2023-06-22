package ru.practicum.shareit.booking.validation;

import ru.practicum.shareit.booking.validation.impl.CorrectDatesValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CorrectDatesValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CorrectDates {
    String message() default "Дата начала должна быть раньше даты конца";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}