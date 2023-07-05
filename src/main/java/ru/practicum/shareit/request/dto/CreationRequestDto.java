package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreationRequestDto {
    @NotNull(message = "Description cannot be empty")
    @Size(min = 1, max = 1024, message = "Description length should not be longer than 1024 characters")
    private String description;
}

