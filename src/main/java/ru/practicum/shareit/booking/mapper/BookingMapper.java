package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreationBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingMapper {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();

        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart().toString());
        bookingDto.setEnd(booking.getEnd().toString());
        bookingDto.setItem(booking.getItem().getId());
        bookingDto.setBooker(booking.getBooker().getId());
        bookingDto.setStatus(bookingDto.getStatus());

        return bookingDto;
    }

    public Booking toBooking(Long userId, CreationBookingDto creationBookingDto) {
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
}
