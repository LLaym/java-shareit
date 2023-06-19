package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final UserDao userDao;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto create(long ownerId, ItemDto itemDto) {
        throwIfUserNotExist(ownerId);

        Item item = itemMapper.toItem(itemDto);
        item.setOwner(userDao.findById(ownerId).get());
        Item itemCreated = itemDao.save(item);

        log.info("Добавлена новая вещь: {}", itemCreated);
        return itemMapper.toItemDto(itemCreated);
    }

    @Override
    public ItemDto getById(long userId, long id) {
        throwIfUserNotExist(userId);

        Item item = itemDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + id + " не найдена"));

        log.info("Передана вещь: {}", item);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(long ownerId, long id, ItemDto itemDto) {
        throwIfUserNotExist(ownerId);

        Item item = itemDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + id + " не найдена"));

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        Item itemUpdated = itemDao.save(item);

        log.info("Обновлена вещь: {}", itemUpdated);
        return itemMapper.toItemDto(itemUpdated);
    }

    @Override
    public List<ItemDto> getAllByOwnerId(long ownerId) {
        throwIfUserNotExist(ownerId);

        List<ItemDto> itemsDto = itemDao.findAllByOwnerId(ownerId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());

        log.info("Передан список вещей пользователя с id {}", ownerId);
        return itemsDto;
    }

    @Override
    public List<ItemDto> getAllBySubstring(long userId, String substring) {
        throwIfUserNotExist(userId);

        List<ItemDto> itemsDtos = itemDao.findAllByNameOrDescriptionContainingIgnoreCase(substring, substring).stream()
                .filter(Item::getAvailable)
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());

        log.info("Передан список найденых вещей по запросу {}", substring);
        return itemsDtos;
    }

    private void throwIfUserNotExist(long userId) {
        userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }
}
