package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreationBookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(long userId, CreationBookingDto creationBookingDto);

    BookingDto confirmStatus(long ownerId, long bookingId, String approved);

    BookingDto getById(long userId, long bookingId);

    List<BookingDto> getAllByUser(long userId, String state);

    List<BookingDto> getAllByItemOwner(long userId, String state);
}
