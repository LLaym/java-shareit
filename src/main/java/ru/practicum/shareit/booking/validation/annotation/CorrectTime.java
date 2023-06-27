package ru.practicum.shareit.booking.validation.annotation;

import ru.practicum.shareit.booking.validation.annotation.impl.CorrectTimeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CorrectTimeValidator.class)
public @interface CorrectTime {
    String message() default "Dates not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}