package ru.practicum.shareit.booking.validation.impl;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.validation.Available;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class AvailableValidator implements ConstraintValidator<Available, Boolean> {
    @Override
    public boolean isValid(Boolean b, ConstraintValidatorContext constraintValidatorContext) {
        return b;
    }
}
