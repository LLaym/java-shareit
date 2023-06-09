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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final ItemMapper itemMapper;
    private final UserDao userDao;

    @Override
    public ItemDto addNewItem(long ownerId, ItemDto itemDto) {
        checkUserExist(ownerId);

        Item item = itemMapper.toItem(itemDto);
        item.setOwnerId(ownerId);
        Item itemCreated = itemDao.save(item);

        log.info("Добавлена новая вещь: {}", itemCreated);
        return itemMapper.toItemDto(itemCreated);
    }

    @Override
    public ItemDto getItemById(long userId, long id) {
        checkUserExist(userId);
        checkItemExist(id);

        Item item = itemDao.findById(id);

        log.info("Передана вещь: {}", item);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(long ownerId, long id, ItemDto itemDto) {
        checkUserExist(ownerId);
        checkItemExist(id);

        Item item = itemDao.findById(id);

        if (item.getOwnerId() != ownerId) {
            throw new NotFoundException("Вещь с id " + id + " не принадлежит пользователю с id " + ownerId);
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        Item itemUpdated = itemDao.update(item);

        log.info("Обновлена вещь: {}", itemUpdated);
        return itemMapper.toItemDto(itemUpdated);
    }

    @Override
    public List<ItemDto> getAllItemsByOwnerId(long ownerId) {
        checkUserExist(ownerId);

        List<Item> items = itemDao.findAllByOwnerId(ownerId);
        List<ItemDto> itemsDtos = new ArrayList<>();

        for (Item item : items) {
            itemsDtos.add(itemMapper.toItemDto(item));
        }

        log.info("Передан список вещей пользователя с id {}: {}", ownerId, items);
        return itemsDtos;
    }

    @Override
    public List<ItemDto> getAllItemsBySubstring(long userId, String substring) {
        checkUserExist(userId);

        List<Item> items = itemDao.findAllBySubstring(substring).stream()
                .filter(Item::getAvailable).collect(Collectors.toList());
        List<ItemDto> itemsDtos = new ArrayList<>();

        for (Item item : items) {
            itemsDtos.add(itemMapper.toItemDto(item));
        }

        log.info("Передан список найденых вещей по запросу {}: {}", substring, items);
        return itemsDtos;
    }

    private void checkItemExist(long id) {
        if (!itemDao.itemExist(id)) {
            throw new NotFoundException("Вещь с id " + id + " не найдена");
        }
    }

    private void checkUserExist(long id) {
        if (!userDao.userExist(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
    }
}
