package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreationCommentDto;
import ru.practicum.shareit.item.dto.ExtendedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item create(Item item);

    ExtendedItemDto getById(long userId, long id);

    Item update(long ownerId, long id, ItemDto itemDto);

    List<ItemDto> getAllByOwnerId(long ownerId);

    List<Item> getAllBySubstring(long userId, String substring);
    CommentDto createComment(long userId, long itemId, CreationCommentDto creationCommentDto);
}
