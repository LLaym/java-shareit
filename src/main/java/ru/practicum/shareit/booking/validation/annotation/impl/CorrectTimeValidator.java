package ru.practicum.shareit.booking.validation.annotation.impl;

import ru.practicum.shareit.booking.dto.CreationBookingDto;
import ru.practicum.shareit.booking.validation.annotation.CorrectTime;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CorrectTimeValidator implements ConstraintValidator<CorrectTime, CreationBookingDto> {
    @Override
    public boolean isValid(CreationBookingDto creationBookingDto, ConstraintValidatorContext constraintValidatorContext) {
        if (creationBookingDto.getStart() == null || creationBookingDto.getEnd() == null) {
            return false;
        }
        LocalDateTime start = LocalDateTime.parse(creationBookingDto.getStart());
        LocalDateTime end = LocalDateTime.parse(creationBookingDto.getEnd());
        if (end.isBefore(LocalDateTime.now())) {
            return false;
        }
        if (end.isBefore(start)) {
            return false;
        }
        if (start.isEqual(end)) {
            return false;
        }
        return !start.isBefore(LocalDateTime.now());
    }
}