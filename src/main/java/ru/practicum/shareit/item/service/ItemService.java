package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item create(Item item);

    ItemDto getById(long userId, long id);

    Item update(long ownerId, long id, ItemDto itemDto);

    List<ItemDto> getAllByOwnerId(long ownerId);

    List<Item> getAllBySubstring(long userId, String substring);
}
