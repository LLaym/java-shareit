package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.*;
import java.time.format.DateTimeFormatter;

/**
 * TODO Sprint add-bookings.
 */

@Getter
@Setter
@NoArgsConstructor
public class CreationBookingDto {
    @Positive
    private Long itemId;
    @NotNull
    @NotBlank
    private String start;
    @NotNull
    @NotEmpty
    private String end;
}
