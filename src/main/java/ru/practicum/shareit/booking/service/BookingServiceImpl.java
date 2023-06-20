package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.exception.NoAccessException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final ItemRepository itemRepository;
    @Override
    public Booking create(Booking booking) {
        booking.setStatus(BookingStatus.WAITING);

        Booking bookingCreated = repository.save(booking);

        log.info("Добавлено новое бронировние: {}", bookingCreated);
        return bookingCreated;
    }

    @Override
    public void confirmStatus(long ownerId, Long bookingId, String approved) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));
        Long itemOwnerId = booking.getItem().getOwner().getId();

        if (ownerId != itemOwnerId) {
            throw new NoAccessException("Пользователь с id " + ownerId + " не является владельцем вещи с id " + itemOwnerId);
        }

        if (approved.equals("true")) {
            booking.setStatus(BookingStatus.APPROVED);
        } else if (approved.equals("false")) {
            booking.setStatus(BookingStatus.REJECTED);
        }
    }

    @Override
    public Booking getById(long userId, Long bookingId) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));
        Long itemOwnerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();

        if (userId != itemOwnerId || userId != bookerId) {
            throw new NoAccessException("Пользователи с id: " + userId + " и " + bookerId  + " не имеют доступа к вещи с id " + itemOwnerId);
        }
        return booking;
    }

    @Override
    public List<Booking> getAllByUser(long userId, String state) {
        List<Booking> bookings =
                repository.findAllByBookerId(userId,Sort.by(Sort.Direction.DESC,"start_date"));

        filterBookings(state, bookings);

        return bookings;
    }

    @Override
    public List<Booking> getAllByItemOwner(long userId, String state) {
        List<Booking> bookings = new ArrayList<>();
        List<Long> itemIds = itemRepository.findAllByOwnerId(userId).stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        for (Long itemId : itemIds) {
            bookings.addAll(repository.findAllByItemId(itemId));
        }

        return bookings;
    }

    private static void filterBookings(String state, List<Booking> bookings) {
        switch (state) {
            case "CURRENT":
                bookings.removeIf(booking -> LocalDateTime.now().isBefore(booking.getStart()) ||
                        LocalDateTime.now().isAfter(booking.getEnd()));
                break;
            case "PAST":
                bookings.removeIf(booking -> LocalDateTime.now().isAfter(booking.getEnd()));
                break;
            case "FUTURE":
                bookings.removeIf(booking -> LocalDateTime.now().isBefore(booking.getStart()));
                break;
            case "WAITING":
                bookings.removeIf(booking -> booking.getStatus() == BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings.removeIf(booking -> booking.getStatus() == BookingStatus.REJECTED);
                break;
        }
    }
}
