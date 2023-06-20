package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreationBookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .itemId(booking.getItem().getId())
                .start(booking.getStart().toString())
                .end(booking.getEnd().toString())
                .build();
    }

    public static Booking toBooking(CreationBookingDto creationBookingDto) {
        return Booking.builder()
                .start(LocalDateTime.parse(creationBookingDto.getStart()))
                .end(LocalDateTime.parse(creationBookingDto.getEnd()))
                .build();
    }
}
