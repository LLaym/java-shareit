package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class CreationCommentDto {
    @NotNull
    @Size(min = 1, max = 1024)
    private String text;
}
