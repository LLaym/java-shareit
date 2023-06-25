package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreationBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.NoAccessException;
import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private static List<Booking> filterBookings(String state, List<Booking> bookings) {
        switch (state) {
            case "CURRENT":
                return bookings.stream()
                        .filter(booking -> LocalDateTime.now().isAfter(booking.getStart()) &&
                                LocalDateTime.now().isBefore(booking.getEnd()))
                        .collect(Collectors.toList());
            case "PAST":
                return bookings.stream()
                        .filter(booking -> LocalDateTime.now().isAfter(booking.getEnd()))
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookings.stream()
                        .filter(booking -> LocalDateTime.now().isBefore(booking.getStart()) || LocalDateTime.now().isBefore(booking.getEnd()))
                        .collect(Collectors.toList());
            case "WAITING":
                return bookings.stream()
                        .filter(booking -> booking.getStatus() == BookingStatus.WAITING)
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookings.stream()
                        .filter(booking -> booking.getStatus() == BookingStatus.REJECTED)
                        .collect(Collectors.toList());
            case "ALL":
                return bookings;
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public BookingDto create(long userId, CreationBookingDto creationBookingDto) {
        Booking booking = BookingMapper.toBooking(userId, creationBookingDto);

        if (booking.getItem().getAvailable().equals(false)) {
            throw new ValidationException("Item is not available for booking");
        }
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("End date not in future");
        }
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException("End date is before start");
        }
        if (booking.getStart().isEqual(booking.getEnd())) {
            throw new ValidationException("Start date is equal to end date");
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Start date is in the past");
        }
        if (Objects.equals(booking.getBooker().getId(), booking.getItem().getOwner().getId())) {
            throw new NotFoundException("You are owner of item");
        }

        booking.setStatus(BookingStatus.WAITING);
        Booking bookingCreated = repository.save(booking);

        log.info("Добавлено новое бронировние: {}", bookingCreated);
        return BookingMapper.toBookingDto(bookingCreated);
    }

    @Override
    public BookingDto confirmStatus(long ownerId, long bookingId, String approved) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));
        Long itemOwnerId = booking.getItem().getOwner().getId();

        if (ownerId != itemOwnerId) {
            throw new NoAccessException("Пользователь с id " + ownerId + " не является владельцем вещи с id " + itemOwnerId);
        }

        if (approved.equals("true") && !booking.getStatus().equals(BookingStatus.APPROVED)) {
            booking.setStatus(BookingStatus.APPROVED);
            repository.save(booking);
        } else if (approved.equals("false")) {
            booking.setStatus(BookingStatus.REJECTED);
            repository.save(booking);
        } else {
            throw new ValidationException("Already approved");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getById(long userId, long bookingId) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));

        Long bookingOwnerId = booking.getBooker().getId();
        Long bookingItemOwnerId = booking.getItem().getOwner().getId();

        if (userId == bookingOwnerId || userId == bookingItemOwnerId) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new NoAccessException("Пользователь с id: " + userId + " не имеет доступа к букингу");
        }
    }

    @Override
    public List<BookingDto> getAllByUser(long userId, String state) {
        User bookingsOwner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        List<Booking> bookings =
                repository.findAllByBooker(bookingsOwner, Sort.by(Sort.Direction.DESC, "start"));

        if (state.isEmpty()) {
            return bookings.stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else {
            return filterBookings(state, bookings).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<BookingDto> getAllByItemOwner(long userId, String state) {
        List<Booking> bookings = new ArrayList<>();
        User itemsOwner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<Item> items = itemRepository.findAllByOwner(itemsOwner);

        for (Item item : items) {
            bookings.addAll(repository.findAllByItem(item));
        }

        bookings.sort((booking1, booking2) -> booking2.getStart().compareTo(booking1.getStart()));

        if (state.isEmpty()) {
            return bookings.stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else {
            return filterBookings(state, bookings).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        }
    }
}
