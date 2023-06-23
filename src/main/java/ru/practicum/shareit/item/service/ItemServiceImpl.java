package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public Item create(Item item) {
        Item itemCreated = repository.save(item);

        log.info("Добавлена новая вещь: {}", itemCreated);
        return itemCreated;
    }

    @Override
    public ItemDto getById(long userId, long id) {
//        throwIfUserNotExist(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        Item item = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + id + " не найдена"));

        ItemDto itemDto = ItemMapper.toItemDto(item);

        log.info("Передана вещь: {}", item);
        if (user.getId() != item.getOwner().getId()) {
            return itemDto;
        } else {
            BookingShortDto lastBookingDto = bookingRepository.findFirstByItemAndEndBeforeOrderByEndDesc(item, LocalDateTime.now())
                    .map(BookingMapper::toBookingShortDto)
                    .orElse(null);
            BookingShortDto nextBookingDto = bookingRepository.findFirstByItemAndStartAfterOrderByStartAsc(item, LocalDateTime.now())
                    .map(BookingMapper::toBookingShortDto)
                    .orElse(null);
            itemDto.setLastBooking(lastBookingDto);
            itemDto.setNextBooking(nextBookingDto);
            return itemDto;
        }
    }

    @Override
    public Item update(long ownerId, long id, ItemDto itemDto) {
        throwIfUserNotExist(ownerId);

        Item item = repository.findById(id)
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

        Item itemUpdated = repository.save(item);

        log.info("Обновлена вещь: {}", itemUpdated);
        return itemUpdated;
    }

    @Override
    public List<ItemDto> getAllByOwnerId(long ownerId) {
//        throwIfUserNotExist(ownerId);
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + ownerId + " не найден"));
        List<Item> items = repository.findAllByOwner(user);

        log.info("Передан список вещей пользователя с id {}", ownerId);
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            ItemDto itemDto = ItemMapper.toItemDto(item);

            BookingShortDto lastBookingDto = bookingRepository.findFirstByItemAndEndBeforeOrderByEndDesc(item, LocalDateTime.now())
                    .map(BookingMapper::toBookingShortDto)
                    .orElse(null);
            BookingShortDto nextBookingDto = bookingRepository.findFirstByItemAndStartAfterOrderByStartAsc(item, LocalDateTime.now())
                    .map(BookingMapper::toBookingShortDto)
                    .orElse(null);
            itemDto.setLastBooking(lastBookingDto);
            itemDto.setNextBooking(nextBookingDto);
            itemDtos.add(itemDto);
        }
        return itemDtos;
    }

    @Override
    public List<Item> getAllBySubstring(long userId, String substring) {
        throwIfUserNotExist(userId);

        if (substring.equals("")) {
            return List.of();
        }

        List<Item> items = repository.findAllByNameOrDescriptionContainingIgnoreCase(substring, substring).stream()
                .filter(Item::getAvailable)
                .collect(Collectors.toList());

        log.info("Передан список найденых вещей по запросу {}", substring);
        return items;
    }

    private void throwIfUserNotExist(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }
}
