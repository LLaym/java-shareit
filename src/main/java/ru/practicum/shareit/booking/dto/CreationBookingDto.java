package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

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
