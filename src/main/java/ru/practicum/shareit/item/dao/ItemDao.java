package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(long ownerId);
    List<Item> findAllByNameOrDescriptionContainingIgnoreCase(String nameSearch, String descriptionSearch);
}