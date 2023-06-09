package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {
    Item save(Item item);

    Item findById(long id);

    Item update(Item item);

    List<Item> findAllByOwnerId(long ownerId);

    List<Item> findAllBySubstring(String substring);

    boolean itemExist(long id);
}
