package ru.practicum.shareit.booking.validation.impl;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.validation.CorrectDates;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

@Component
public class CorrectDatesValidator implements ConstraintValidator<CorrectDates, Booking> {
    @Override
    public boolean isValid(Booking booking, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime startDate = booking.getStart();
        LocalDateTime endDate = booking.getEnd();

        Boolean condition1 = endDate.isBefore(LocalDateTime.now());
        Boolean condition2 = startDate.isBefore(endDate);
        Boolean condition3 = !startDate.isEqual(endDate);
        Boolean condition4 = !startDate.isAfter(LocalDateTime.now());

        return condition1 && condition2 && condition3 && condition4;
    }
}
