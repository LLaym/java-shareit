package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

public interface ItemService {
    ItemDto addNewItem(long ownerId, ItemDto itemDto);

    ItemDto getItemById(long id);

    ItemDto updateItem(ItemDto itemDto);

    void deleteItemById(long id);
}
