package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreationBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                             @RequestBody CreationBookingDto creationBookingDto) {
        Booking booking = bookingMapper.toBooking(userId, creationBookingDto);

        return bookingMapper.toBookingDto(bookingService.create(booking));
    }

//    @PatchMapping("/{bookingId}?approved={approved}")
//    public void confirmStatus(@RequestHeader("X-Sharer-User-Id") long ownerId,
//                              @PathVariable Long bookingId,
//                              @RequestParam(name = "approved") String approved) {
//        bookingService.confirmStatus(ownerId, bookingId, approved);
//    }
//
//    @GetMapping("/{bookingId}")
//    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
//                              @PathVariable Long bookingId) {
//        return bookingMapper.toBookingDto(bookingService.getById(userId, bookingId));
//    }
//
//    @GetMapping
//    public List<BookingDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") long userId,
//                                   @RequestParam(name = "state", defaultValue = "ALL") String state) {
//        return bookingService.getAllByUser(userId, state).stream()
//                .map(bookingMapper::toBookingDto)
//                .collect(Collectors.toList());
//    }
//
//    @GetMapping("/owner?state={state}")
//    public List<BookingDto> getAllByItemOwner(@RequestHeader("X-Sharer-User-Id") long userId,
//                                   @RequestParam(name = "state", defaultValue = "ALL") String state) {
//        return bookingService.getAllByItemOwner(userId, state).stream()
//                .map(bookingMapper::toBookingDto)
//                .collect(Collectors.toList());
//    }
}
