package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.validation.group.AddNewItemAction;
import ru.practicum.shareit.item.validation.group.UpdateItemAction;

import javax.validation.constraints.Min;
import javax.validation.groups.Default;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addNewItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                              @RequestBody @Validated({Default.class, AddNewItemAction.class}) ItemDto itemDto) {
        return itemService.addNewItem(ownerId, itemDto);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                               @PathVariable @Min(1L) long id) {
        return itemService.getItemById(userId, id);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                              @PathVariable @Min(1L) long id,
                              @RequestBody @Validated({Default.class, UpdateItemAction.class}) ItemDto itemDto) {
        return itemService.updateItem(ownerId, id, itemDto);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemService.getAllItemsByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> getAllItemsBySubstring(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam(name = "text") String substring) {
        return itemService.getAllItemsBySubstring(userId, substring);
    }
}
