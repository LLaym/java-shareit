package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

/**
 * TODO Sprint add-bookings.
 */

@Getter
@Setter
@NoArgsConstructor
public class BookingShortDto {
    private Long id;
    private Long bookerId;
}
