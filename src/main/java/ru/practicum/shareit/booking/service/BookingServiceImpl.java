package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreationBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.NoAccessException;
import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Transactional
    @Override
    public BookingDto create(long userId, CreationBookingDto creationBookingDto) {
        Booking booking = bookingMapper.toBooking(userId, creationBookingDto);
        if (booking.getItem().getAvailable().equals(false)) {
            throw new ValidationException("Item is not available for booking");
        }
        if (Objects.equals(booking.getBooker().getId(), booking.getItem().getOwner().getId())) {
            throw new NotFoundException("You are owner of item");
        }

        booking.setStatus(WAITING);
        Booking bookingCreated = repository.save(booking);

        log.info("Booking created: {}", bookingCreated);
        return bookingMapper.toBookingDto(bookingCreated);
    }

    @Transactional
    @Override
    public BookingDto confirmStatus(long ownerId, long bookingId, String approved) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " not found"));
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User with id " + ownerId + " not found"));
        long bookingItemOwnerId = booking.getItem().getOwner().getId();
        long userId = user.getId();

        if (userId != bookingItemOwnerId) {
            throw new NoAccessException("User with id " + ownerId + " is not owner of Item with id " + bookingItemOwnerId);
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

        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getById(long userId, long bookingId) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " not found"));

        Long bookingOwnerId = booking.getBooker().getId();
        Long bookingItemOwnerId = booking.getItem().getOwner().getId();

        if (userId == bookingOwnerId || userId == bookingItemOwnerId) {
            return bookingMapper.toBookingDto(booking);
        } else {
            throw new NoAccessException("User with id: " + userId + " does not have permission to booking");
        }
    }

    @Override
    public List<BookingDto> getAllByUser(long userId, String state, int from, int size) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Sort sort = Sort.by("start").descending();
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size, sort);

        List<Booking> bookings =
                repository.findAllByBooker(booker, pageRequest);

        if (state.isEmpty()) {
            return bookings.stream()
                    .map(bookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else {
            return filterBookings(state, bookings).stream()
                    .map(bookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<BookingDto> getAllByItemOwner(long userId, String state, int from, int size) {
        User itemsOwner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Sort sort = Sort.by("start").descending();
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size, sort);

        List<Booking> bookings =
                repository.findAllByItemOwner(itemsOwner, pageRequest);

        if (state.isEmpty()) {
            return bookings.stream()
                    .map(bookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else {
            return filterBookings(state, bookings).stream()
                    .map(bookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        }
    }

    private List<Booking> filterBookings(String state, List<Booking> bookings) {
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
                        .filter(booking -> booking.getStatus() == WAITING)
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
}
