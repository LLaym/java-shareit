package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ExtendedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemMapper {
    private final CommentMapper commentMapper;

    public ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setOwnerId(item.getOwner().getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;
    }

    public Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public ExtendedItemDto toExtendedItemDto(Item item) {
        ExtendedItemDto extendedItemDto = new ExtendedItemDto();
        extendedItemDto.setId(item.getId());
        extendedItemDto.setOwnerId(item.getOwner().getId());
        extendedItemDto.setName(item.getName());
        extendedItemDto.setDescription(item.getDescription());
        extendedItemDto.setAvailable(item.getAvailable());
        extendedItemDto.setComments(item.getComments().stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList()));
        return extendedItemDto;
    }
}
