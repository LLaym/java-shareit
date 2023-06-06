package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public Item add(@RequestHeader("X-Later-User-Id") Long ownerId,
                    @RequestBody @Valid Item item) {
        return itemService.addNewItem(ownerId, item);
    }

    @GetMapping("/{id}")
    public ItemDto get(@PathVariable long id) {
        return itemService.getItemById(id);
    }

    @PutMapping
    public Item update(@RequestBody @Valid Item item) {
        return itemService.updateItem(item);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        itemService.deleteItemById(id);
    }
}
