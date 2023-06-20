package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO Sprint add-bookings.
 */

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreationBookingDto {
    private Long itemId;
    private String start;
    private String end;
}
