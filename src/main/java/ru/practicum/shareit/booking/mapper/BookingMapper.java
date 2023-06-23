package ru.practicum.shareit.booking.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.CreationBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.time.LocalDateTime;

@Service
public class BookingMapper {
    private static ItemRepository itemRepository;
    private static UserRepository userRepository;

    @Autowired
    public BookingMapper(ItemRepository itemRepository, UserRepository userRepository) {
        BookingMapper.itemRepository = itemRepository;
        BookingMapper.userRepository = userRepository;
    }


    public static BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();

        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart().toString());
        bookingDto.setEnd(booking.getEnd().toString());
        bookingDto.setItem(ItemMapper.toItemDto(booking.getItem()));
        bookingDto.setBooker(UserMapper.toUserDto(booking.getBooker()));
        bookingDto.setStatus(booking.getStatus().toString());

        return bookingDto;
    }

    public static Booking toBooking(Long userId, CreationBookingDto creationBookingDto) {
        Booking booking = new Booking();

        booking.setBooker(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " +
                        userId + " не существует")));
        booking.setItem(itemRepository.findById(creationBookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Предмет с id " +
                        creationBookingDto.getItemId() + " не существует")));
        booking.setStart(LocalDateTime.parse(creationBookingDto.getStart()));
        booking.setEnd(LocalDateTime.parse(creationBookingDto.getEnd()));

        return booking;
    }

    public static BookingShortDto toBookingShortDto(Booking booking) {
        BookingShortDto bookingShortDto = new BookingShortDto();

        bookingShortDto.setId(booking.getId());
        bookingShortDto.setBookerId(booking.getBooker().getId());

        return bookingShortDto;
    }
}
