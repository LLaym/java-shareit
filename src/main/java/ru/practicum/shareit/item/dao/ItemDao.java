package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

public interface ItemDao {
    Item save(Item item);
    Item findById(long id);
    Item update(Item item);
    void deleteById(long id);
}
