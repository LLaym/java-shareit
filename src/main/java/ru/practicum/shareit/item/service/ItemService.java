package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(long ownerId, ItemDto itemDto);

    ItemDto getById(long userId, long id);

    ItemDto update(long ownerId, long id, ItemDto itemDto);

    List<ItemDto> getAllByOwnerId(long ownerId);

    List<ItemDto> getAllBySubstring(long userId, String substring);
}
