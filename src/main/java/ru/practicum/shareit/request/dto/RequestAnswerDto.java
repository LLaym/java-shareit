package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;

@Getter @Setter
public class RequestAnswerDto {
    private Long itemId;
    private String itemName;
    private Long ownerId;
}
