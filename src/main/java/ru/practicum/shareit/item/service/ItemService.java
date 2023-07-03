package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreationCommentDto;
import ru.practicum.shareit.item.dto.ExtendedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(long ownerId, ItemDto itemDto);

    ExtendedItemDto getById(long userId, long id);

    ItemDto update(long ownerId, long id, ItemDto itemDto);

    List<ItemDto> getAllByOwnerId(long ownerId, int from, int size);

    List<ItemDto> getAllBySubstring(long userId, String substring, int from, int size);

    CommentDto createComment(long userId, long itemId, CreationCommentDto creationCommentDto);
}
