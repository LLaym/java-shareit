package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreationBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                             @RequestBody @Valid CreationBookingDto creationBookingDto) {
        return bookingService.create(userId, creationBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto confirmStatus(@RequestHeader("X-Sharer-User-Id") @Positive long ownerId,
                                    @PathVariable @Positive Long bookingId,
                                    @RequestParam(name = "approved") String approved) {
        return bookingService.confirmStatus(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                              @PathVariable @Positive Long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                         @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.getAllByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByItemOwner(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                              @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.getAllByItemOwner(userId, state);
    }
}
