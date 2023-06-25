package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreationCommentDto;
import ru.practicum.shareit.item.dto.ExtendedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.exception.ValidationException;

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
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemDto create(long ownerId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found")));
        Item itemCreated = repository.save(item);

        log.info("Добавлена новая вещь: {}", itemCreated);
        return ItemMapper.toItemDto(itemCreated);
    }

    @Override
    public ExtendedItemDto getById(long userId, long id) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        Item item = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + id + " не найдена"));

        ExtendedItemDto extendedItemDto = ItemMapper.toExtendedItemDto(item);

        log.info("Передана вещь: {}", item);
        if (user.getId() != item.getOwner().getId()) {
            return extendedItemDto;
        } else {
            BookingShortDto lastBookingDto = bookingRepository.findFirstByItemAndStartBeforeOrderByEndDesc(item, LocalDateTime.now())
                    .filter(booking -> booking.getBooker().getId() != user.getId())
                    .filter(booking -> booking.getStatus() != BookingStatus.REJECTED)
                    .map(BookingMapper::toBookingShortDto)
                    .orElse(null);
            BookingShortDto nextBookingDto = bookingRepository.findFirstByItemAndStartAfterOrderByStartAsc(item, LocalDateTime.now())
                    .filter(booking -> booking.getBooker().getId() != user.getId())
                    .filter(booking -> booking.getStatus() != BookingStatus.REJECTED)
                    .map(BookingMapper::toBookingShortDto)
                    .orElse(null);
            extendedItemDto.setLastBooking(lastBookingDto);
            extendedItemDto.setNextBooking(nextBookingDto);
            return extendedItemDto;
        }
    }

    @Override
    public ItemDto update(long ownerId, long id, ItemDto itemDto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + ownerId + " не найден"));
        Item item = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + id + " не найдена"));

        if (item.getOwner().getId() != owner.getId()) {
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
        return ItemMapper.toItemDto(itemUpdated);
    }

    @Override
    public List<ItemDto> getAllByOwnerId(long ownerId) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + ownerId + " не найден"));
        List<Item> items = repository.findAllByOwner(user);

        log.info("Передан список вещей пользователя с id {}", ownerId);
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            ItemDto itemDto = ItemMapper.toItemDto(item);

            BookingShortDto lastBookingDto = bookingRepository.findFirstByItemAndStartBeforeOrderByEndDesc(item, LocalDateTime.now())
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
    public List<ItemDto> getAllBySubstring(long userId, String substring) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        if (substring.equals("")) {
            return List.of();
        }

        List<ItemDto> items = repository.findAllByNameOrDescriptionContainingIgnoreCase(substring, substring).stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        log.info("Передан список найденых вещей по запросу {}", substring);
        return items;
    }

    @Override
    public CommentDto createComment(long userId, long itemId, CreationCommentDto creationCommentDto) {
        Comment comment = CommentMapper.toComment(creationCommentDto);
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));

        Booking booking = bookingRepository.findFirstByBookerAndItemAndStatusAndEndBefore(booker, item, BookingStatus.APPROVED, LocalDateTime.now())
                .orElseThrow(() -> new ValidationException("Booking not found"));


        comment.setItem(item);
        comment.setUser(booker);
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }
}
