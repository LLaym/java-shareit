package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

/**
 * TODO Sprint add-bookings.
 */

@Getter
@Setter
@NoArgsConstructor
public class BookingDto {
    private Long id;
    private String start;
    private String end;
    private ItemDto item;
    private UserDto booker;
    private String status;
}
