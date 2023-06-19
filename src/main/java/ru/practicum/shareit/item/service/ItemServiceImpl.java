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

    @Override
    public Item create(Item item) {
        Item itemCreated = itemDao.save(item);

        log.info("Добавлена новая вещь: {}", itemCreated);
        return itemCreated;
    }

    @Override
    public Item getById(long userId, long id) {
        throwIfUserNotExist(userId);

        Item item = itemDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + id + " не найдена"));

        log.info("Передана вещь: {}", item);
        return item;
    }

    @Override
    public Item update(long ownerId, long id, ItemDto itemDto) {
        throwIfUserNotExist(ownerId);

        Item item = itemDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + id + " не найдена"));

        if (item.getOwner().getId() != ownerId) {
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

        Item itemUpdated = itemDao.save(item);

        log.info("Обновлена вещь: {}", itemUpdated);
        return itemUpdated;
    }

    @Override
    public List<Item> getAllByOwnerId(long ownerId) {
        throwIfUserNotExist(ownerId);

        List<Item> items = itemDao.findAllByOwnerId(ownerId);

        log.info("Передан список вещей пользователя с id {}", ownerId);
        return items;
    }

    @Override
    public List<Item> getAllBySubstring(long userId, String substring) {
        throwIfUserNotExist(userId);

        if (substring.equals("")) {
            return List.of();
        }

        List<Item> items = itemDao.findAllByNameOrDescriptionContainingIgnoreCase(substring, substring).stream()
                .filter(Item::getAvailable)
                .collect(Collectors.toList());

        log.info("Передан список найденых вещей по запросу {}", substring);
        return items;
    }

    private void throwIfUserNotExist(long userId) {
        userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }
}
