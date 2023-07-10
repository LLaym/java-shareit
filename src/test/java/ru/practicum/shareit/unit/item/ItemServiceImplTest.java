package ru.practicum.shareit.unit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
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
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private ItemMapper itemMapper;
    @InjectMocks
    private ItemServiceImpl itemService;
    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;
    @Captor
    private ArgumentCaptor<Comment> commentArgumentCaptor;

    @Test
    void create_whenUserFoundAndItemRequestIdNull_thenCreateAndItemDtoReturned() {
        long ownerId = 1L;

        ItemDto itemDto = new ItemDto();
        itemDto.setOwnerId(ownerId);

        Item item = new Item();
        User user = new User();

        when(itemMapper.toItem(itemDto)).thenReturn(item);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        ItemDto resultItemDto = itemService.create(ownerId, itemDto);

        assertEquals(itemDto, resultItemDto);
        assertEquals(itemDto.getOwnerId(), ownerId);
        assertNull(itemDto.getRequestId());
        verify(itemMapper, times(1)).toItem(itemDto);
        verify(userRepository, times(1)).findById(ownerId);
        verify(itemRepository, times(1)).save(item);
        verifyNoMoreInteractions(itemMapper, userRepository, itemRepository);
    }

    @Test
    void create_whenUserFoundAndItemRequestFound_thenCreateAndItemDtoReturned() {
        long ownerId = 1L;
        long requestId = 1L;

        ItemDto itemDto = new ItemDto();
        itemDto.setRequestId(requestId);
        itemDto.setOwnerId(ownerId);

        Item item = new Item();
        User user = new User();

        Request request = new Request();
        request.setId(requestId);

        when(itemMapper.toItem(itemDto)).thenReturn(item);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(requestRepository.findById(itemDto.getRequestId())).thenReturn(Optional.of(request));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        ItemDto resultItemDto = itemService.create(ownerId, itemDto);

        assertEquals(itemDto, resultItemDto);
        assertEquals(resultItemDto.getOwnerId(), ownerId);
        assertEquals(resultItemDto.getRequestId(), requestId);
        verify(itemMapper, times(1)).toItem(itemDto);
        verify(userRepository, times(1)).findById(ownerId);
        verify(requestRepository, times(1)).findById(requestId);
        verify(itemRepository, times(1)).save(item);
        verifyNoMoreInteractions(itemMapper, userRepository, requestRepository, itemRepository);
    }

    @Test
    void create_whenUserFoundAndItemRequestNotFound_thenNotFondExceptionThrown() {
        long ownerId = 1L;
        long requestId = 999L;

        ItemDto itemDto = new ItemDto();
        itemDto.setRequestId(requestId);

        Item item = new Item();
        User user = new User();

        when(itemMapper.toItem(itemDto)).thenReturn(item);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(ownerId, itemDto));
        verify(itemMapper, times(1)).toItem(itemDto);
        verify(userRepository, times(1)).findById(ownerId);
        verify(requestRepository, times(1)).findById(requestId);
        verifyNoMoreInteractions(itemMapper, userRepository, requestRepository);
    }

    @Test
    void create_whenUserFoundAndUserNotFound_thenNotFondExceptionThrown() {
        long ownerId = 1L;

        ItemDto itemDto = new ItemDto();
        Item item = new Item();

        when(itemMapper.toItem(itemDto)).thenReturn(item);
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(ownerId, itemDto));
        verify(itemMapper, times(1)).toItem(itemDto);
        verify(userRepository, times(1)).findById(ownerId);
        verifyNoMoreInteractions(itemMapper, userRepository);
    }

    @Test
    void getById_whenNotOwner_thenRequestDtoReturned() {
        long userId = 1L;
        long id = 1L;

        User user = new User();
        Item item = new Item();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        ItemDto itemDto = new ItemDto();
        itemDto.setOwnerId(2L);

        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        ItemDto resultItemDto = itemService.getById(userId, id);

        assertEquals(itemDto, resultItemDto);
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(id);
        verify(itemMapper, times(1)).toItemDto(item);
        verifyNoMoreInteractions(userRepository, itemRepository, itemMapper);
    }

    @Test
    void getById_whenOwnerWithBookings_thenRequestDtoReturned() {
        long userId = 1L;
        long id = 1L;

        User user = new User();
        Item item = new Item();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        ItemDto itemDto = new ItemDto();
        itemDto.setOwnerId(userId);

        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        Booking lastBooking = new Booking();
        Booking nextBooking = new Booking();
        BookingShortDto lastBookingShortDto = new BookingShortDto();
        BookingShortDto nextBookingShortDto = new BookingShortDto();

        when(bookingRepository.findFirstByItemAndStartBeforeAndStatusNotOrderByEndDesc(any(Item.class),
                any(LocalDateTime.class),
                any(BookingStatus.class)))
                .thenReturn(Optional.of(lastBooking));
        when(bookingMapper.toBookingShortDto(lastBooking))
                .thenReturn(lastBookingShortDto);
        when(bookingRepository.findFirstByItemAndStartAfterAndStatusNotOrderByStartAsc(any(Item.class),
                any(LocalDateTime.class),
                any(BookingStatus.class)))
                .thenReturn(Optional.of(nextBooking));
        when(bookingMapper.toBookingShortDto(nextBooking))
                .thenReturn(nextBookingShortDto);

        ItemDto resultItemDto = itemService.getById(userId, id);

        assertEquals(itemDto, resultItemDto);
        assertNotNull(resultItemDto.getLastBooking());
        assertNotNull(resultItemDto.getNextBooking());
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(id);
        verify(itemMapper, times(1)).toItemDto(item);
        verify(bookingRepository, times(1))
                .findFirstByItemAndStartBeforeAndStatusNotOrderByEndDesc(any(Item.class),
                        any(LocalDateTime.class),
                        any(BookingStatus.class));
        verify(bookingRepository, times(1))
                .findFirstByItemAndStartAfterAndStatusNotOrderByStartAsc(any(Item.class),
                        any(LocalDateTime.class),
                        any(BookingStatus.class));
        verify(bookingMapper, times(2)).toBookingShortDto(any(Booking.class));
        verifyNoMoreInteractions(userRepository, itemRepository, itemMapper, bookingRepository, bookingMapper);
    }

    @Test
    void getById_whenOwnerWithEmptyBookings_thenRequestDtoReturned() {
        long userId = 1L;
        long id = 1L;

        User user = new User();
        Item item = new Item();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        ItemDto itemDto = new ItemDto();
        itemDto.setOwnerId(userId);

        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        when(bookingRepository.findFirstByItemAndStartBeforeAndStatusNotOrderByEndDesc(any(Item.class),
                any(LocalDateTime.class),
                any(BookingStatus.class)))
                .thenReturn(Optional.empty());
        when(bookingRepository.findFirstByItemAndStartAfterAndStatusNotOrderByStartAsc(any(Item.class),
                any(LocalDateTime.class),
                any(BookingStatus.class)))
                .thenReturn(Optional.empty());

        ItemDto resultItemDto = itemService.getById(userId, id);

        assertEquals(itemDto, resultItemDto);
        assertNull(resultItemDto.getLastBooking());
        assertNull(resultItemDto.getNextBooking());
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(id);
        verify(itemMapper, times(1)).toItemDto(item);
        verify(bookingRepository, times(1))
                .findFirstByItemAndStartBeforeAndStatusNotOrderByEndDesc(any(Item.class),
                        any(LocalDateTime.class),
                        any(BookingStatus.class));
        verify(bookingRepository, times(1))
                .findFirstByItemAndStartAfterAndStatusNotOrderByStartAsc(any(Item.class),
                        any(LocalDateTime.class),
                        any(BookingStatus.class));
        verifyNoMoreInteractions(userRepository, itemRepository, itemMapper, bookingRepository);
    }

    @Test
    void getById_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 999L;
        long id = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getById(userId, id));
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getById_whenItemNotFound_thenNotFoundExceptionThrown() {
        long userId = 1L;
        long id = 999L;

        User user = new User();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getById(userId, id));
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(id);
        verifyNoMoreInteractions(userRepository, itemRepository);
    }

    @Test
    void update_whenSuccess_thenItemDtoReturned() {
        long ownerId = 1L;
        long id = 1L;

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Name updated");
        itemDto.setDescription("Updated description");
        itemDto.setAvailable(true);

        User owner = new User(1L, "John", "john@example.com");

        Item existingItem = new Item();
        existingItem.setOwner(owner);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(id)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(existingItem);
        when(itemMapper.toItemDto(any(Item.class))).thenReturn(new ItemDto());

        ItemDto updatedItemDto = itemService.update(ownerId, id, itemDto);

        assertNotNull(updatedItemDto);

        verify(itemMapper).toItemDto(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();

        assertNotNull(savedItem);
        assertEquals(itemDto.getName(), savedItem.getName());
        assertEquals(itemDto.getDescription(), savedItem.getDescription());
        assertEquals(itemDto.getAvailable(), savedItem.getAvailable());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(id);
        verify(itemRepository, times(1)).save(any(Item.class));
        verify(itemMapper, times(1)).toItemDto(any(Item.class));
        verifyNoMoreInteractions(userRepository, itemRepository, itemMapper);
    }

    @Test
    void update_whenUserNotFound_thenNotFoundExceptionThrown() {
        long ownerId = 999L;
        long id = 1L;

        ItemDto itemDto = new ItemDto();

        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.update(ownerId, id, itemDto));
        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void update_whenItemNotFound_thenNotFoundExceptionThrown() {
        long ownerId = 1L;
        long id = 999L;

        ItemDto itemDto = new ItemDto();
        User user = new User();

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.update(ownerId, id, itemDto));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository, itemRepository);
    }

    @Test
    void update_whenUnauthorizedAccess_thenNotFoundExceptionThrown() {
        long ownerId = 1L;
        long id = 1L;

        ItemDto itemDto = new ItemDto();
        User owner = new User(1L, "John", "john@example.com");

        Item existingItem = new Item();
        existingItem.setOwner(new User(2L, "Nick", "nick@example.com"));

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(id)).thenReturn(Optional.of(existingItem));


        assertThrows(NotFoundException.class, () -> itemService.update(ownerId, id, itemDto));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository, itemRepository);
    }

    @Test
    void getAllByOwnerId_whenOwnerFoundWithBookings_thenCollectionOfItemDtoReturned() {
        long ownerId = 1L;

        Item item = new Item();
        ItemDto itemDto = new ItemDto();

        Booking lastBooking = new Booking();
        lastBooking.setId(1L);

        Booking nextBooking = new Booking();
        nextBooking.setId(2L);

        BookingShortDto lastBookingShortDto = new BookingShortDto();
        BookingShortDto nextBookingShortDto = new BookingShortDto();

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(new User()));
        when(itemRepository.findAllByOwner(any(User.class), any(PageRequest.class)))
                .thenReturn(List.of(item));
        when(itemMapper.toItemDto(item))
                .thenReturn(itemDto);
        when(bookingRepository.findFirstByItemAndStartBeforeAndStatusNotOrderByEndDesc(any(Item.class),
                any(LocalDateTime.class),
                any(BookingStatus.class)))
                .thenReturn(Optional.of(lastBooking));
        when(bookingRepository.findFirstByItemAndStartAfterAndStatusNotOrderByStartAsc(any(Item.class),
                any(LocalDateTime.class),
                any(BookingStatus.class)))
                .thenReturn(Optional.of(nextBooking));
        when(bookingMapper.toBookingShortDto(lastBooking))
                .thenReturn(lastBookingShortDto);
        when(bookingMapper.toBookingShortDto(nextBooking))
                .thenReturn(nextBookingShortDto);

        List<ItemDto> resultItemDtos = itemService.getAllByOwnerId(ownerId, 0, 10);

        assertNotNull(resultItemDtos);
        assertEquals(1, resultItemDtos.size());
        assertEquals(itemDto, resultItemDtos.get(0));
        assertSame(lastBookingShortDto, itemDto.getLastBooking());
        assertSame(nextBookingShortDto, itemDto.getNextBooking());
        verify(userRepository, times(1))
                .findById(anyLong());
        verify(itemRepository, times(1))
                .findAllByOwner(any(User.class), any(PageRequest.class));
        verify(itemMapper, times(1))
                .toItemDto(any(Item.class));
        verify(bookingRepository, times(1))
                .findFirstByItemAndStartBeforeAndStatusNotOrderByEndDesc(any(Item.class),
                        any(LocalDateTime.class),
                        any(BookingStatus.class));
        verify(bookingRepository, times(1))
                .findFirstByItemAndStartAfterAndStatusNotOrderByStartAsc(any(Item.class),
                        any(LocalDateTime.class),
                        any(BookingStatus.class));
        verify(bookingMapper, times(2))
                .toBookingShortDto(any(Booking.class));
        verifyNoMoreInteractions(userRepository,
                itemRepository,
                itemMapper,
                bookingRepository,
                bookingMapper);
    }

    @Test
    void getAllByOwnerId_whenOwnerFoundWithoutBookings_thenCollectionOfItemDtoReturned() {
        long ownerId = 1L;

        Item item = new Item();
        ItemDto itemDto = new ItemDto();

        Booking lastBooking = new Booking();
        lastBooking.setId(1L);

        Booking nextBooking = new Booking();
        nextBooking.setId(2L);

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(new User()));
        when(itemRepository.findAllByOwner(any(User.class), any(PageRequest.class)))
                .thenReturn(List.of(item));
        when(itemMapper.toItemDto(item))
                .thenReturn(itemDto);
        when(bookingRepository.findFirstByItemAndStartBeforeAndStatusNotOrderByEndDesc(any(Item.class),
                any(LocalDateTime.class),
                any(BookingStatus.class)))
                .thenReturn(Optional.empty());
        when(bookingRepository.findFirstByItemAndStartAfterAndStatusNotOrderByStartAsc(any(Item.class),
                any(LocalDateTime.class),
                any(BookingStatus.class)))
                .thenReturn(Optional.empty());

        List<ItemDto> resultItemDtos = itemService.getAllByOwnerId(ownerId, 0, 10);

        assertNotNull(resultItemDtos);
        assertEquals(1, resultItemDtos.size());
        assertEquals(itemDto, resultItemDtos.get(0));
        assertNull(itemDto.getLastBooking());
        assertNull(itemDto.getNextBooking());
        verify(userRepository, times(1))
                .findById(anyLong());
        verify(itemRepository, times(1))
                .findAllByOwner(any(User.class), any(PageRequest.class));
        verify(itemMapper, times(1))
                .toItemDto(any(Item.class));
        verify(bookingRepository, times(1))
                .findFirstByItemAndStartBeforeAndStatusNotOrderByEndDesc(any(Item.class),
                        any(LocalDateTime.class),
                        any(BookingStatus.class));
        verify(bookingRepository, times(1))
                .findFirstByItemAndStartAfterAndStatusNotOrderByStartAsc(any(Item.class),
                        any(LocalDateTime.class),
                        any(BookingStatus.class));
        verifyNoMoreInteractions(userRepository,
                itemRepository,
                itemMapper,
                bookingRepository);
    }

    @Test
    void getAllByOwnerId_whenUserNotFound_thenNotFoundExceptionThrown() {
        long ownerId = 1L;

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getAllByOwnerId(ownerId, 0, 10));
        verify(userRepository, times(1))
                .findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAllBySubstring_whenItemAvailable_thenCollectionOfItemDtoReturned() {
        long userId = 1L;
        String substring = "hAm";

        Item item = new Item();
        item.setAvailable(true);

        ItemDto itemDto = new ItemDto();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(new User()));
        when(itemRepository.findBySearchTermIgnoreCase(anyString(),
                any(PageRequest.class)))
                .thenReturn(List.of(item));
        when(itemMapper.toItemDto(item))
                .thenReturn(itemDto);

        List<ItemDto> resultItems = itemService.getAllBySubstring(userId, substring, 0, 10);

        assertFalse(resultItems.isEmpty());
        assertEquals(1, resultItems.size());
        assertEquals(itemDto, resultItems.get(0));
        verify(userRepository, times(1))
                .findById(anyLong());
        verify(itemRepository, times(1))
                .findBySearchTermIgnoreCase(anyString(),
                        any(PageRequest.class));
        verify(itemMapper, times(1))
                .toItemDto(any(Item.class));
        verifyNoMoreInteractions(userRepository, itemRepository, itemMapper);
    }

    @Test
    void getAllBySubstring_whenItemNotAvailable_thenEmptyCollectionOfItemDtoReturned() {
        long userId = 1L;
        String substring = "hAm";

        Item item = new Item();
        item.setAvailable(false);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(new User()));
        when(itemRepository.findBySearchTermIgnoreCase(anyString(),
                any(PageRequest.class)))
                .thenReturn(List.of(item));

        List<ItemDto> resultItems = itemService.getAllBySubstring(userId, substring, 0, 10);

        assertTrue(resultItems.isEmpty());
        verify(userRepository, times(1))
                .findById(anyLong());
        verify(itemRepository, times(1))
                .findBySearchTermIgnoreCase(anyString(),
                        any(PageRequest.class));
        verifyNoMoreInteractions(userRepository, itemRepository);
    }

    @Test
    void getAllBySubstring_whenEmptySearch_thenEmptyCollectionOfItemDtoReturned() {
        long userId = 1L;
        String substring = "";

        Item item = new Item();
        item.setAvailable(false);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(new User()));

        List<ItemDto> resultItems = itemService.getAllBySubstring(userId, substring, 0, 10);

        assertTrue(resultItems.isEmpty());
        verify(userRepository, times(1))
                .findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void createComment_whenSuccess_thenCreateCommentAndReturnCommentDto() {
        long userId = 1L;
        long itemId = 1L;

        User user = new User();
        Item item = new Item();
        Comment comment = new Comment();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByBookerAndItemAndStatusAndEndBefore(
                any(User.class),
                any(Item.class),
                any(BookingStatus.class),
                any(LocalDateTime.class)))
                .thenReturn(Optional.of(new Booking()));
        when(commentMapper.toComment(any(CreationCommentDto.class)))
                .thenReturn(comment);
        when(commentRepository.save(comment))
                .thenReturn(comment);
        when(commentMapper.toCommentDto(comment))
                .thenReturn(new CommentDto());

        CommentDto commentDto = itemService.createComment(userId, itemId, new CreationCommentDto());

        assertNotNull(commentDto);
        verify(commentMapper).toCommentDto(commentArgumentCaptor.capture());
        Comment resultComment = commentArgumentCaptor.getValue();
        assertEquals(comment, resultComment);
        assertEquals(user, resultComment.getUser());
        assertEquals(item, resultComment.getItem());
        assertTrue(resultComment.getCreated().isBefore(LocalDateTime.now()));
        verify(userRepository, times(1))
                .findById(anyLong());
        verify(itemRepository, times(1))
                .findById(anyLong());
        verify(bookingRepository, times(1))
                .findFirstByBookerAndItemAndStatusAndEndBefore(
                        any(User.class),
                        any(Item.class),
                        any(BookingStatus.class),
                        any(LocalDateTime.class));
        verify(commentMapper, times(1)).toComment(any(CreationCommentDto.class));
        verify(commentRepository, times(1)).save(comment);
        verify(commentMapper, times(1)).toCommentDto(any(Comment.class));
        verifyNoMoreInteractions(userRepository,
                itemRepository,
                bookingRepository,
                commentMapper,
                commentRepository
        );
    }

    @Test
    void createComment_whenBookingNotFound_thenValidationExceptionThrown() {
        long userId = 1L;
        long itemId = 1L;

        User user = new User();
        Item item = new Item();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByBookerAndItemAndStatusAndEndBefore(
                any(User.class),
                any(Item.class),
                any(BookingStatus.class),
                any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        assertThrows(ValidationException.class,
                () -> itemService.createComment(userId, itemId, new CreationCommentDto()));

        verify(userRepository, times(1))
                .findById(anyLong());
        verify(itemRepository, times(1))
                .findById(anyLong());
        verify(bookingRepository, times(1))
                .findFirstByBookerAndItemAndStatusAndEndBefore(
                        any(User.class),
                        any(Item.class),
                        any(BookingStatus.class),
                        any(LocalDateTime.class));
        verifyNoMoreInteractions(userRepository, itemRepository, bookingRepository);
    }

    @Test
    void createComment_whenItemNotFound_thenNotFoundExceptionThrown() {
        long userId = 1L;
        long itemId = 999L;

        User user = new User();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.createComment(userId, itemId, new CreationCommentDto()));

        verify(userRepository, times(1))
                .findById(anyLong());
        verify(itemRepository, times(1))
                .findById(anyLong());
        verifyNoMoreInteractions(userRepository, itemRepository);
    }

    @Test
    void createComment_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 999L;
        long itemId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.createComment(userId, itemId, new CreationCommentDto()));

        verify(userRepository, times(1))
                .findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }
}