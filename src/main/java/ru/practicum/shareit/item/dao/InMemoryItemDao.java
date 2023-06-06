package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryItemDao implements ItemDao {
    private final Map<Long, Item> items = new HashMap<>();
    private Long nextId = 0L;

    @Override
    public Item save(Item item) {
        item.setId(nextId++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item findById(long id) {
        return items.get(id);
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void deleteById(long id) {
        items.remove(id);
    }
}
