package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemDao implements ItemDao {
    private final Map<Long, Item> items = new HashMap<>();
    private Long nextId = 1L;

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
    public List<Item> findAllByOwnerId(long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findAllBySubstring(String substring) {
        String query = substring.toLowerCase();

        if (query.equals("")) {
            return Collections.emptyList();
        } else {
            return items.values().stream()
                    .filter(item -> (item.getName().toLowerCase().contains(query))
                            || (item.getDescription().toLowerCase().contains(query)))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public boolean itemExist(long id) {
        for (Item item : items.values()) {
            if (item.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }
}
