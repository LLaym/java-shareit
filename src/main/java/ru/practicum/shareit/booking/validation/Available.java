package ru.practicum.shareit.booking.validation;

import ru.practicum.shareit.booking.validation.impl.AvailableValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AvailableValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Available {
    String message() default "Предмет не доступен для шеринга";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
