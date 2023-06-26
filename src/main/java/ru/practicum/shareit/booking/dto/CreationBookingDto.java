package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
public class CreationBookingDto {
    @Positive(message = "Item id should be positive")
    private Long itemId;
    @NotNull(message = "Start time cannot be null")
    @NotBlank(message = "Start time cannot be empty")
    private String start;
    @NotNull(message = "End time cannot be null")
    @NotBlank(message = "End time cannot be empty")
    private String end;
}
