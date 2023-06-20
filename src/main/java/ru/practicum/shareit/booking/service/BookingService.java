package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;
import java.util.List;

public interface BookingService {
    Booking create(Booking booking);

    void confirmStatus(long ownerId, Long bookingId, String approved);

    Booking getById(long userId, Long bookingId);

    List<Booking> getAllByUser(long userId, String state);

    List<Booking> getAllByItemOwner(long userId, String state);
}
