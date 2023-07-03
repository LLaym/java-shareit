package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Getter @Setter
public class RequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private Long requestorId;
    private List<ItemDto> items;
}
