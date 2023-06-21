package ru.practicum.shareit.booking.dto;

import lombok.*;

/**
 * TODO Sprint add-bookings.
 */

@Getter
@Setter
@NoArgsConstructor
public class CreationBookingDto {
    private Long itemId;
    private String start;
    private String end;
}
