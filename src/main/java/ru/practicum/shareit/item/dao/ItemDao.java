package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemDao {
    Item save(Item item);

    Optional<Item> findById(long id);

    Item update(Item item);

    List<Item> findAllByOwnerId(long ownerId);

    List<Item> findAllBySubstring(String substring);
}
