package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.validation.group.AddNewItemAction;
import ru.practicum.shareit.item.validation.group.UpdateItemAction;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.constraints.Min;
import javax.validation.groups.Default;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long ownerId,
                          @RequestBody @Validated({Default.class, AddNewItemAction.class}) ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userService.getById(ownerId));

        return ItemMapper.toItemDto(itemService.create(item));
    }

    @GetMapping("/{id}")
    public ItemDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable @Min(1L) long id) {
        return ItemMapper.toItemDto(itemService.getById(userId, id));
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long ownerId,
                          @PathVariable @Min(1L) long id,
                          @RequestBody @Validated({Default.class, UpdateItemAction.class}) ItemDto itemDto) {
        return ItemMapper.toItemDto(itemService.update(ownerId, id, itemDto));
    }

    @GetMapping
    public List<ItemDto> getAllByOwnerId(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemService.getAllByOwnerId(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> getAllBySubstring(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestParam(name = "text") String substring) {
        return itemService.getAllBySubstring(userId, substring).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
