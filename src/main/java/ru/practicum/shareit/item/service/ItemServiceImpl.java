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
    private final ItemMapper itemMapper;
    private final UserDao userDao;

    @Override
    public ItemDto create(long ownerId, ItemDto itemDto) {
        userDao.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + ownerId + " не найден"));

        Item item = itemMapper.toItem(itemDto);
        item.setOwnerId(ownerId);
        Item itemCreated = itemDao.save(item);

        log.info("Добавлена новая вещь: {}", itemCreated);
        return itemMapper.toItemDto(itemCreated);
    }

    @Override
    public ItemDto getById(long userId, long id) {
        userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Item item = itemDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + id + " не найдена"));

        log.info("Передана вещь: {}", item);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(long ownerId, long id, ItemDto itemDto) {
        userDao.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + ownerId + " не найден"));

        Item item = itemDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + id + " не найдена"));

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
    public List<ItemDto> getAllByOwnerId(long ownerId) {
        userDao.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + ownerId + " не найден"));

        List<ItemDto> itemsDtos = itemDao.findAllByOwnerId(ownerId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());

        log.info("Передан список вещей пользователя с id {}", ownerId);
        return itemsDtos;
    }

    @Override
    public List<ItemDto> getAllBySubstring(long userId, String substring) {
        userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        List<ItemDto> itemsDtos = itemDao.findAllBySubstring(substring).stream()
                .filter(Item::getAvailable)
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());

        log.info("Передан список найденых вещей по запросу {}", substring);
        return itemsDtos;
    }
}
