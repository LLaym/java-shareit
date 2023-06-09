package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addNewItem(long ownerId, ItemDto itemDto);

    ItemDto getItemById(long userId, long id);

    ItemDto updateItem(long ownerId, long id, ItemDto itemDto);

    void deleteItemById(long id);

    List<ItemDto> getAllItemsByOwnerId(long ownerId);

    List<ItemDto> getAllItemsBySubstring(long userId, String substring);
}
