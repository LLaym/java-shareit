package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.validation.group.CreateAction;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.NoAccessException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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
    @Override
    public Booking create(Booking booking) {
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
        return bookingCreated;
    }

    @Override
    public Booking confirmStatus(long ownerId, Long bookingId, String approved) {
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

        return booking;
    }

    @Override
    public Booking getById(long userId, Long bookingId) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));

        Long bookingOwnerId = booking.getBooker().getId();
        Long bookingItemOwnerId = booking.getItem().getOwner().getId();

        if (userId == bookingOwnerId || userId == bookingItemOwnerId) {
            return booking;
        } else {
            throw new NoAccessException("Пользователь с id: " + userId + " не имеет доступа к букингу");
        }
    }

    @Override
    public List<Booking> getAllByUser(long userId, String state) {
        User bookingsOwner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        List<Booking> bookings =
                repository.findAllByBooker(bookingsOwner,Sort.by(Sort.Direction.DESC,"start"));

        if (state.isEmpty()) {
            return bookings;
        } else {
            return filterBookings(state, bookings);
        }
    }

    @Override
    public List<Booking> getAllByItemOwner(long userId, String state) {
        List<Booking> bookings = new ArrayList<>();

        User itemsOwner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<Item> items = itemRepository.findAllByOwner(itemsOwner);

        for (Item item : items) {
            bookings.addAll(repository.findAllByItem(item));
        }

        bookings.sort((booking1, booking2) -> booking2.getStart().compareTo(booking1.getStart()));

        if (state.isEmpty()) {
            return bookings;
        } else {
            return filterBookings(state, bookings);
        }
    }

    private static List<Booking> filterBookings(String state, List<Booking> bookings) {
        List<Booking> filteredBookings = new ArrayList<>();

        switch (state) {
            case "CURRENT":
                filteredBookings = bookings.stream()
                        .filter(booking -> LocalDateTime.now().isAfter(booking.getStart()) &&
                        LocalDateTime.now().isBefore(booking.getEnd()))
                        .collect(Collectors.toList());
                break;
            case "PAST":
                filteredBookings = bookings.stream()
                        .filter(booking -> LocalDateTime.now().isAfter(booking.getEnd()))
                        .collect(Collectors.toList());
                break;
            case "FUTURE":
                filteredBookings = bookings.stream()
                        .filter(booking -> LocalDateTime.now().isBefore(booking.getStart()) || LocalDateTime.now().isBefore(booking.getEnd()))
                        .collect(Collectors.toList());
                break;
            case "WAITING":
                filteredBookings = bookings.stream()
                        .filter(booking -> booking.getStatus() == BookingStatus.WAITING)
                        .collect(Collectors.toList());
                break;
            case "REJECTED":
                filteredBookings = bookings.stream()
                        .filter(booking -> booking.getStatus() == BookingStatus.REJECTED)
                        .collect(Collectors.toList());
                break;
            case "ALL":
                filteredBookings = bookings;
                break;
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return filteredBookings;
    }
}
