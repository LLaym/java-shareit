package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.CreationBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingMapper {
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;


    public BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart().toString());
        bookingDto.setEnd(booking.getEnd().toString());
        bookingDto.setItem(itemMapper.toItemDto(booking.getItem()));
        bookingDto.setBooker(userMapper.toUserDto(booking.getBooker()));
        bookingDto.setStatus(booking.getStatus().toString());
        return bookingDto;
    }

    public Booking toBooking(CreationBookingDto creationBookingDto) {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.parse(creationBookingDto.getStart()));
        booking.setEnd(LocalDateTime.parse(creationBookingDto.getEnd()));
        return booking;
    }

    public BookingShortDto toBookingShortDto(Booking booking) {
        BookingShortDto bookingShortDto = new BookingShortDto();
        bookingShortDto.setId(booking.getId());
        bookingShortDto.setBookerId(booking.getBooker().getId());
        return bookingShortDto;
    }
}
