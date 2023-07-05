package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreationCommentDto {
    @NotNull(message = "Text should not be empty")
    @Size(min = 1, max = 1024, message = "Text length should not be longer than 1024 characters")
    private String text;
}
