package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreationCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.RequestRepository;
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
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;
    private final ItemMapper itemMapper;

    @Transactional
    @Override
    public ItemDto create(long ownerId, ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found")));

        if (itemDto.getRequestId() != null) {
            item.setRequest(requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Request not found")));
        }

        Item itemCreated = itemRepository.save(item);

        log.info("New Item added: {}", itemCreated);
        return itemMapper.toItemDto(itemCreated);
    }

    @Override
    public ItemDto getById(long userId, long id) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item with id " + id + " not found"));

        ItemDto itemDto = itemMapper.toItemDto(item);

        log.info("Privded Item: {}", item);
        if (userId == itemDto.getOwnerId()) {
            itemDto.setLastBooking(getLastBookingShortDtoByItem(item));
            itemDto.setNextBooking(getNextBookingShortDtoByItem(item));
            return itemDto;
        } else {
            return itemDto;
        }
    }

    @Transactional
    @Override
    public ItemDto update(long ownerId, long id, ItemDto itemDto) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User with id " + ownerId + " not found"));
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item with id " + id + " not found"));

        if (item.getOwner().getId() == ownerId) {
            updateItemFields(itemDto, item);

            Item itemUpdated = itemRepository.save(item);

            log.info("Item updated: {}", itemUpdated);
            return itemMapper.toItemDto(itemUpdated);
        } else {
            throw new NotFoundException("User with id " + ownerId + " does not have access to Item with id " + id);
        }
    }

    @Override
    public List<ItemDto> getAllByOwnerId(long ownerId, int from, int size) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User with id " + ownerId + " not found"));

        Sort sort = Sort.sort(Item.class).by(Item::getId);

        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size, sort);

        List<Item> items = itemRepository.findAllByOwner(user, pageRequest);
        List<ItemDto> itemDtos = toItemDto(items);

        log.info("Provided list of Items of User with id {}", ownerId);
        return itemDtos;
    }

    @Override
    public List<ItemDto> getAllBySubstring(long userId, String substring, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

        if (substring.equals("")) {
            return List.of();
        }

        Sort sort = Sort.unsorted();
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size, sort);

        List<ItemDto> items = itemRepository.findBySearchTermIgnoreCase(substring,
                        pageRequest).stream()
                .filter(Item::getAvailable)
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());

        log.info("Provided list of Items by querry {}", substring);
        return items;
    }

    @Transactional
    @Override
    public CommentDto createComment(long userId, long itemId, CreationCommentDto creationCommentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));
        bookingRepository.findFirstByBookerAndItemAndStatusAndEndBefore(
                        user,
                        item,
                        BookingStatus.APPROVED,
                        LocalDateTime.now())
                .orElseThrow(() -> new ValidationException("Booking not found"));

        Comment comment = commentMapper.toComment(creationCommentDto);
        comment.setItem(item);
        comment.setUser(user);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);

        log.info("New Item added: {}", savedComment);
        return commentMapper.toCommentDto(savedComment);
    }

    private BookingShortDto getLastBookingShortDtoByItem(Item item) {
        return bookingRepository.findFirstByItemAndStartBeforeAndStatusNotOrderByEndDesc(item,
                        LocalDateTime.now(),
                        BookingStatus.REJECTED)
                .map(bookingMapper::toBookingShortDto)
                .orElse(null);
    }

    private BookingShortDto getNextBookingShortDtoByItem(Item item) {
        return bookingRepository.findFirstByItemAndStartAfterAndStatusNotOrderByStartAsc(item,
                        LocalDateTime.now(),
                        BookingStatus.REJECTED)
                .map(bookingMapper::toBookingShortDto)
                .orElse(null);
    }

    private void updateItemFields(ItemDto itemDto, Item item) {
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
    }

    private List<ItemDto> toItemDto(List<Item> items) {
        // TODO: 07.07.2023 Maybe replace to ItemMapper?
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            ItemDto itemDto = itemMapper.toItemDto(item);

            BookingShortDto lastBookingDto
                    = bookingRepository.findFirstByItemAndStartBeforeAndStatusNotOrderByEndDesc(item,
                            LocalDateTime.now(),
                            BookingStatus.REJECTED)
                    .map(bookingMapper::toBookingShortDto)
                    .orElse(null);
            BookingShortDto nextBookingDto
                    = bookingRepository.findFirstByItemAndStartAfterAndStatusNotOrderByStartAsc(item,
                            LocalDateTime.now(),
                            BookingStatus.REJECTED)
                    .map(bookingMapper::toBookingShortDto)
                    .orElse(null);
            itemDto.setLastBooking(lastBookingDto);
            itemDto.setNextBooking(nextBookingDto);
            itemDtos.add(itemDto);
        }
        return itemDtos;
    }
}
