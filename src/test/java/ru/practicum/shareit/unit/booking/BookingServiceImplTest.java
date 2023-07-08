package ru.practicum.shareit.unit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreationBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.NoAccessException;
import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Captor
    ArgumentCaptor<Booking> bookingArgumentCaptor;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingMapper bookingMapper;
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void create_whenSuccess_thenCreateAndBookingDtoReturned() {
        long userId = 1L;
        long itemId = 1L;

        CreationBookingDto creationBookingDto = new CreationBookingDto();
        creationBookingDto.setItemId(itemId);

        User booker = new User(userId, "John", "john@example.com");

        Item item = new Item();
        item.setOwner(new User(2L, "Nick", "nick@example.com"));
        item.setAvailable(true);

        Booking booking = new Booking();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(creationBookingDto.getItemId()))
                .thenReturn(Optional.of(item));
        when(bookingMapper.toBooking(creationBookingDto))
                .thenReturn(booking);
        when(bookingRepository.save(booking))
                .thenReturn(booking);
        when(bookingMapper.toBookingDto(booking))
                .thenReturn(new BookingDto());

        BookingDto result = bookingService.create(userId, creationBookingDto);

        assertNotNull(result);
        verify(bookingRepository).save(bookingArgumentCaptor.capture());

        Booking savedBooking = bookingArgumentCaptor.getValue();
        assertEquals(booking, savedBooking);
        assertEquals(booker, savedBooking.getBooker());
        assertEquals(item, savedBooking.getItem());
        assertEquals(BookingStatus.WAITING, savedBooking.getStatus());
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingMapper, times(1)).toBooking(creationBookingDto);
        verify(bookingRepository, times(1)).save(booking);
        verify(bookingMapper, times(1)).toBookingDto(booking);
        verifyNoMoreInteractions(userRepository, itemRepository, bookingMapper, bookingRepository);
    }

    @Test
    void create_whenItemNotAvailable_thenValidationExceptionThrown() {
        long userId = 1L;
        long itemId = 1L;

        CreationBookingDto creationBookingDto = new CreationBookingDto();
        creationBookingDto.setItemId(itemId);

        User booker = new User(userId, "John", "john@example.com");

        Item item = new Item();
        item.setOwner(new User(2L, "Nick", "nick@example.com"));
        item.setAvailable(false);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(creationBookingDto.getItemId()))
                .thenReturn(Optional.of(item));

        assertThrows(ValidationException.class,
                () -> bookingService.create(userId, creationBookingDto));
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verifyNoMoreInteractions(userRepository, itemRepository);
    }

    @Test
    void create_whenBookerIsOwnerOfItem_thenNotFoundExceptionThrown() {
        long userId = 1L;
        long itemId = 1L;

        CreationBookingDto creationBookingDto = new CreationBookingDto();
        creationBookingDto.setItemId(itemId);

        User booker = new User(userId, "John", "john@example.com");

        Item item = new Item();
        item.setOwner(booker);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(creationBookingDto.getItemId()))
                .thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class,
                () -> bookingService.create(userId, creationBookingDto));
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verifyNoMoreInteractions(userRepository, itemRepository);
    }

    @Test
    void create_whenItemNotFound_thenNotFoundExceptionThrown() {
        long userId = 1L;
        long itemId = 999L;

        CreationBookingDto creationBookingDto = new CreationBookingDto();
        creationBookingDto.setItemId(itemId);

        User booker = new User(userId, "John", "john@example.com");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(creationBookingDto.getItemId()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.create(userId, creationBookingDto));
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verifyNoMoreInteractions(userRepository, itemRepository);
    }

    @Test
    void create_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 999L;

        CreationBookingDto creationBookingDto = new CreationBookingDto();

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.create(userId, creationBookingDto));
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void confirmStatus_whenFlagTrue_thenSaveAndBookingDtoReturned() {
        long ownerId = 1L;
        long bookingId = 1L;
        String flag = "true";

        User owner = new User(ownerId, "John", "john@example.com");

        Item item = new Item();
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.save(booking))
                .thenReturn(booking);
        when(bookingMapper.toBookingDto(booking))
                .thenReturn(new BookingDto());

        BookingDto result = bookingService.confirmStatus(ownerId, bookingId, flag);

        assertNotNull(result);
        verify(bookingMapper).toBookingDto(bookingArgumentCaptor.capture());
        Booking savedBooking = bookingArgumentCaptor.getValue();

        assertEquals(booking, savedBooking);
        assertEquals(item, savedBooking.getItem());
        assertEquals(owner, savedBooking.getItem().getOwner());
        assertEquals(BookingStatus.APPROVED, savedBooking.getStatus());
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(userRepository, times(1)).findById(ownerId);
        verify(bookingRepository, times(1)).save(booking);
        verify(bookingMapper, times(1)).toBookingDto(booking);
        verifyNoMoreInteractions(bookingRepository, userRepository, bookingMapper);
    }

    @Test
    void confirmStatus_whenFlagFalse_thenSaveAndBookingDtoReturned() {
        long ownerId = 1L;
        long bookingId = 1L;
        String flag = "false";

        User owner = new User(ownerId, "John", "john@example.com");

        Item item = new Item();
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.save(booking))
                .thenReturn(booking);
        when(bookingMapper.toBookingDto(booking))
                .thenReturn(new BookingDto());

        BookingDto result = bookingService.confirmStatus(ownerId, bookingId, flag);

        assertNotNull(result);
        verify(bookingMapper).toBookingDto(bookingArgumentCaptor.capture());
        Booking savedBooking = bookingArgumentCaptor.getValue();

        assertEquals(booking, savedBooking);
        assertEquals(item, savedBooking.getItem());
        assertEquals(owner, savedBooking.getItem().getOwner());
        assertEquals(BookingStatus.REJECTED, savedBooking.getStatus());
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(userRepository, times(1)).findById(ownerId);
        verify(bookingRepository, times(1)).save(booking);
        verify(bookingMapper, times(1)).toBookingDto(booking);
        verifyNoMoreInteractions(bookingRepository, userRepository, bookingMapper);
    }

    @Test
    void confirmStatus_whenBookingStatusUnableToConfirm_thenValidationExceptionThrown() {
        long ownerId = 1L;
        long bookingId = 1L;
        String flag = "true";

        User owner = new User(ownerId, "John", "john@example.com");

        Item item = new Item();
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setStatus(BookingStatus.CANCELED);

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));

        assertThrows(ValidationException.class, () -> bookingService.confirmStatus(ownerId, bookingId, flag));
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(userRepository, times(1)).findById(ownerId);
        verifyNoMoreInteractions(bookingRepository, userRepository);
    }

    @Test
    void confirmStatus_whenNotOwnerOfItem_thenNoAccessExceptionThrown() {
        long ownerId = 1L;
        long bookingId = 1L;
        String flag = "true";

        User owner = new User(ownerId, "John", "john@example.com");

        Item item = new Item();
        item.setOwner(new User(2L, "Jack", "jack@example.com"));

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setStatus(BookingStatus.CANCELED);

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));

        assertThrows(NoAccessException.class, () -> bookingService.confirmStatus(ownerId, bookingId, flag));
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(userRepository, times(1)).findById(ownerId);
        verifyNoMoreInteractions(bookingRepository, userRepository);
    }

    @Test
    void confirmStatus_whenUserNotFound_thenNotFoundExceptionThrown() {
        long ownerId = 999L;
        long bookingId = 1L;
        String flag = "true";

        Booking booking = new Booking();

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.confirmStatus(ownerId, bookingId, flag));
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(userRepository, times(1)).findById(ownerId);
        verifyNoMoreInteractions(bookingRepository, userRepository);
    }

    @Test
    void confirmStatus_whenBookingNotFound_thenNotFoundExceptionThrown() {
        long ownerId = 1L;
        long bookingId = 999L;
        String flag = "true";

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.confirmStatus(ownerId, bookingId, flag));
        verify(bookingRepository, times(1)).findById(bookingId);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getById_whenOwnerOfItem_thenBookingDtoReturned() {
        long userId = 1L;
        long bookingId = 1L;

        Item item = new Item();
        item.setOwner(new User(userId, "John", "john@example.com"));

        Booking booking = new Booking();
        booking.setBooker(new User(2L, "Danny", "danny@example.com"));
        booking.setItem(item);

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingDto(booking))
                .thenReturn(new BookingDto());

        BookingDto resultBookingDto = bookingService.getById(userId, bookingId);

        assertNotNull(resultBookingDto);
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingMapper, times(1)).toBookingDto(booking);
        verifyNoMoreInteractions(bookingRepository, bookingMapper);
    }

    @Test
    void getById_whenBooker_thenBookingDtoReturned() {
        long userId = 1L;
        long bookingId = 1L;

        Item item = new Item();
        item.setOwner(new User(2L, "John", "john@example.com"));

        Booking booking = new Booking();
        booking.setBooker(new User(userId, "Danny", "danny@example.com"));
        booking.setItem(item);

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingDto(booking))
                .thenReturn(new BookingDto());

        BookingDto resultBookingDto = bookingService.getById(userId, bookingId);

        assertNotNull(resultBookingDto);
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingMapper, times(1)).toBookingDto(booking);
        verifyNoMoreInteractions(bookingRepository, bookingMapper);
    }

    @Test
    void getById_whenNoAccess_thenNoAccessExceptionThrown() {
        long userId = 1L;
        long bookingId = 1L;

        Item item = new Item();
        item.setOwner(new User(2L, "John", "john@example.com"));

        Booking booking = new Booking();
        booking.setBooker(new User(3L, "Danny", "danny@example.com"));
        booking.setItem(item);

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));

        assertThrows(NoAccessException.class, () -> bookingService.getById(userId, bookingId));
        verify(bookingRepository, times(1)).findById(bookingId);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getById_whenBookingNotFound_thenNotFoundExceptionThrown() {
        long userId = 1L;
        long bookingId = 999L;

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getById(userId, bookingId));
        verify(bookingRepository, times(1)).findById(bookingId);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getAllByUser_whenStateCurrent_thenCollectionOfBookingDtoReturned() {
        long userId = 1L;
        String state = "CURRENT";

        User booker = new User();
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBooker(any(User.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingDto(booking))
                .thenReturn(new BookingDto());

        List<BookingDto> result = bookingService.getAllByUser(userId, state, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(bookingMapper).toBookingDto(bookingArgumentCaptor.capture());

        Booking resultBooking = bookingArgumentCaptor.getValue();

        assertSame(booking, resultBooking);
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findAllByBooker(any(User.class), any(PageRequest.class));
        verify(bookingMapper, times(1)).toBookingDto(booking);
        verifyNoMoreInteractions(userRepository, bookingRepository, bookingMapper);
    }

    @Test
    void getAllByUser_whenStatePast_thenCollectionOfBookingDtoReturned() {
        long userId = 1L;
        String state = "PAST";

        User booker = new User();
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBooker(any(User.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingDto(booking))
                .thenReturn(new BookingDto());

        List<BookingDto> result = bookingService.getAllByUser(userId, state, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(bookingMapper).toBookingDto(bookingArgumentCaptor.capture());

        Booking resultBooking = bookingArgumentCaptor.getValue();

        assertSame(booking, resultBooking);
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findAllByBooker(any(User.class), any(PageRequest.class));
        verify(bookingMapper, times(1)).toBookingDto(booking);
        verifyNoMoreInteractions(userRepository, bookingRepository, bookingMapper);
    }

    @Test
    void getAllByUser_whenStateFuture_thenCollectionOfBookingDtoReturned() {
        long userId = 1L;
        String state = "FUTURE";

        User booker = new User();
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBooker(any(User.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingDto(booking))
                .thenReturn(new BookingDto());

        List<BookingDto> result = bookingService.getAllByUser(userId, state, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(bookingMapper).toBookingDto(bookingArgumentCaptor.capture());

        Booking resultBooking = bookingArgumentCaptor.getValue();

        assertSame(booking, resultBooking);
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findAllByBooker(any(User.class), any(PageRequest.class));
        verify(bookingMapper, times(1)).toBookingDto(booking);
        verifyNoMoreInteractions(userRepository, bookingRepository, bookingMapper);
    }

    @Test
    void getAllByUser_whenStateWaiting_thenCollectionOfBookingDtoReturned() {
        long userId = 1L;
        String state = "WAITING";

        User booker = new User();
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBooker(any(User.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingDto(booking))
                .thenReturn(new BookingDto());

        List<BookingDto> result = bookingService.getAllByUser(userId, state, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(bookingMapper).toBookingDto(bookingArgumentCaptor.capture());

        Booking resultBooking = bookingArgumentCaptor.getValue();

        assertSame(booking, resultBooking);
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findAllByBooker(any(User.class), any(PageRequest.class));
        verify(bookingMapper, times(1)).toBookingDto(booking);
        verifyNoMoreInteractions(userRepository, bookingRepository, bookingMapper);
    }

    @Test
    void getAllByUser_whenStateRejected_thenCollectionOfBookingDtoReturned() {
        long userId = 1L;
        String state = "REJECTED";

        User booker = new User();
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.REJECTED);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBooker(any(User.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingDto(booking))
                .thenReturn(new BookingDto());

        List<BookingDto> result = bookingService.getAllByUser(userId, state, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(bookingMapper).toBookingDto(bookingArgumentCaptor.capture());

        Booking resultBooking = bookingArgumentCaptor.getValue();

        assertSame(booking, resultBooking);
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findAllByBooker(any(User.class), any(PageRequest.class));
        verify(bookingMapper, times(1)).toBookingDto(booking);
        verifyNoMoreInteractions(userRepository, bookingRepository, bookingMapper);
    }

    @Test
    void getAllByUser_whenStateAll_thenCollectionOfBookingDtoReturned() {
        long userId = 1L;
        String state = "ALL";

        User booker = new User();
        Booking booking = new Booking();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBooker(any(User.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingDto(booking))
                .thenReturn(new BookingDto());

        List<BookingDto> result = bookingService.getAllByUser(userId, state, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(bookingMapper).toBookingDto(bookingArgumentCaptor.capture());

        Booking resultBooking = bookingArgumentCaptor.getValue();

        assertSame(booking, resultBooking);
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findAllByBooker(any(User.class), any(PageRequest.class));
        verify(bookingMapper, times(1)).toBookingDto(booking);
        verifyNoMoreInteractions(userRepository, bookingRepository, bookingMapper);
    }

    @Test
    void getAllByUser_whenStateEmpty_thenCollectionOfBookingDtoReturned() {
        long userId = 1L;
        String state = "";

        User booker = new User();
        Booking booking = new Booking();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBooker(any(User.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingDto(booking))
                .thenReturn(new BookingDto());

        List<BookingDto> result = bookingService.getAllByUser(userId, state, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(bookingMapper).toBookingDto(bookingArgumentCaptor.capture());

        Booking resultBooking = bookingArgumentCaptor.getValue();

        assertSame(booking, resultBooking);
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findAllByBooker(any(User.class), any(PageRequest.class));
        verify(bookingMapper, times(1)).toBookingDto(booking);
        verifyNoMoreInteractions(userRepository, bookingRepository, bookingMapper);
    }

    @Test
    void getAllByUser_whenStateUnknown_thenValidationExceptionThrown() {
        long userId = 1L;
        String state = "Unknown state";

        User booker = new User();
        Booking booking = new Booking();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBooker(any(User.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.getAllByUser(userId, state, 0, 10));

        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findAllByBooker(any(User.class), any(PageRequest.class));
        verifyNoMoreInteractions(userRepository, bookingRepository);
    }

    @Test
    void getAllByUser_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 999L;
        String state = "CURRENT";

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getAllByUser(userId, state, 0, 10));

        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAllByItemOwner_whenStateCurrent_thenCollectionOfBookingDtoReturned() {
        long userId = 1L;
        String state = "ALL";

        User itemOwner = new User();
        Booking booking = new Booking();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(itemOwner));
        when(bookingRepository.findAllByItemOwner(any(User.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingDto(booking))
                .thenReturn(new BookingDto());

        List<BookingDto> result = bookingService.getAllByItemOwner(userId, state, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(bookingMapper).toBookingDto(bookingArgumentCaptor.capture());

        Booking resultBooking = bookingArgumentCaptor.getValue();

        assertSame(booking, resultBooking);
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findAllByItemOwner(any(User.class), any(PageRequest.class));
        verify(bookingMapper, times(1)).toBookingDto(booking);
        verifyNoMoreInteractions(userRepository, bookingRepository, bookingMapper);
    }
}