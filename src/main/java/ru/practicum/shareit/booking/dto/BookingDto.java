package ru.practicum.shareit.booking.dto;

import lombok.*;

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
    private Long item;
    private Long booker;
    private String status;
}
