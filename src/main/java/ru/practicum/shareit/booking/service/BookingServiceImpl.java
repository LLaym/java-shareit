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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.NoAccessException;
import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Transactional
    @Override
    public BookingDto create(long userId, CreationBookingDto creationBookingDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        "User with id " + userId + " does not exist"));
        Item item = itemRepository.findById(creationBookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException(
                        "Item with id " + creationBookingDto.getItemId() + " does not exist"));

        if (booker.getId().equals(item.getOwner().getId())) {
            throw new NotFoundException("You are owner of item");
        }
        if (item.getAvailable().equals(false)) {
            throw new ValidationException("Item is not available for booking");
        }

        Booking booking = bookingMapper.toBooking(creationBookingDto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(WAITING);

        Booking bookingCreated = bookingRepository.save(booking);

        log.info("Booking created: {}", bookingCreated);
        return bookingMapper.toBookingDto(bookingCreated);
    }

    @Transactional
    @Override
    public BookingDto confirmStatus(long ownerId, long bookingId, String flag) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " not found"));
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User with id " + ownerId + " not found"));

        long itemOwnerId = booking.getItem().getOwner().getId();

        if (owner.getId() == itemOwnerId) {
            setStatusOrThrow(flag, booking);
            bookingRepository.save(booking);
        } else {
            throw new NoAccessException("User with id " + ownerId + " is not owner of Item with id " + itemOwnerId);
        }

        log.info("Confirm status of booking with id {}", bookingId);
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getById(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " not found"));

        Long bookerId = booking.getBooker().getId();
        Long itemOwnerId = booking.getItem().getOwner().getId();

        if (userId == itemOwnerId || userId == bookerId) {
            log.info("Provided Booking: {}", booking);
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

        List<Booking> bookings = filterBookingsByState(
                bookingRepository.findAllByBooker(booker, pageRequest),
                state);

        log.info("Provided all Bookings by User");
        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllByItemOwner(long userId, String state, int from, int size) {
        User itemOwner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Sort sort = Sort.by("start").descending();
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size, sort);

        List<Booking> bookings = filterBookingsByState(
                bookingRepository.findAllByItemOwner(itemOwner, pageRequest),
                state);

        log.info("Provided all Bookings by Item owner");
        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private void setStatusOrThrow(String flag, Booking booking) {
        if (booking.getStatus().equals(WAITING)) {
            if (flag.equals("true")) {
                booking.setStatus(BookingStatus.APPROVED);
            }
            if (flag.equals("false")) {
                booking.setStatus(BookingStatus.REJECTED);
            }
        } else {
            throw new ValidationException("Cannot confirm with status " + booking.getStatus());
        }
    }

    private List<Booking> filterBookingsByState(List<Booking> bookings, String state) {
        if (state.isEmpty()) {
            return bookings;
        }
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
