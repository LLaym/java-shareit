package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public interface ItemService {
    Item addNewItem(long ownerId, Item item);
    ItemDto getItemById(long id);
    Item updateItem(Item item);
    void deleteItemById(long id);
}
