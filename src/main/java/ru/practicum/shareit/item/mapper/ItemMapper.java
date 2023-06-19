package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.util.exception.NotFoundException;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    private final UserDao userDao;

    public ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(),
                item.getOwner().getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable());
    }

    public Item toItem(ItemDto itemDto) {
        return new Item(itemDto.getId(),
                userDao.findById(itemDto.getOwnerId())
                        .orElseThrow(() -> new NotFoundException("Пользователь не найден")),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable());
    }
}
