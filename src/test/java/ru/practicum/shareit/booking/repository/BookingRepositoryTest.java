package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private User booker;
    private User itemOwner;
    private Item item;
    private Booking booking;

    @BeforeEach
    public void addData() {
        booker = new User(null, "John", "john@example.com");
        itemOwner = new User(null, "Danny", "danny@example.com");

        userRepository.save(booker);
        userRepository.save(itemOwner);

        item = new Item();
        item.setOwner(itemOwner);
        item.setName("Hammer");
        item.setDescription("Handy tool");
        item.setAvailable(false);

        itemRepository.save(item);

        booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);

        bookingRepository.save(booking);
    }

    @Test
    void findAllByBooker_whenPersist_thenCollectionOfBookingsReturned() {
        List<Booking> maybeBookers = bookingRepository.findAllByBooker(
                booker,
                PageRequest.of(0, 10)
        );

        assertFalse(maybeBookers.isEmpty());
        assertEquals(1, maybeBookers.size());

        Booking actualBooking = maybeBookers.get(0);
        assertSame(booking, actualBooking);
    }

    @Test
    void findFirstByItemAndStartBeforeAndStatusNotOrderByEndDesc_whenPersist_thenOptionalOfBookingReturned() {
        Optional<Booking> maybeBooker = bookingRepository.findFirstByItemAndStartBeforeAndStatusNotOrderByEndDesc(
                item,
                LocalDateTime.now().plusDays(10),
                BookingStatus.CANCELED
        );

        assertTrue(maybeBooker.isPresent());
        assertSame(booking, maybeBooker.get());
    }

    @Test
    void findFirstByItemAndStartAfterAndStatusNotOrderByStartAsc_whenPersist_thenOptionalOfBookingReturned() {
        Optional<Booking> maybeBooker = bookingRepository.findFirstByItemAndStartAfterAndStatusNotOrderByStartAsc(
                item,
                LocalDateTime.now(),
                BookingStatus.CANCELED
        );

        assertTrue(maybeBooker.isPresent());
        assertSame(booking, maybeBooker.get());
    }

    @Test
    void findFirstByBookerAndItemAndStatusAndEndBefore_whenPersist_thenOptionalOfBookingReturned() {
        Optional<Booking> maybeBooker = bookingRepository.findFirstByBookerAndItemAndStatusAndEndBefore(
                booker,
                item,
                BookingStatus.APPROVED,
                LocalDateTime.now().plusDays(3)
        );

        assertTrue(maybeBooker.isPresent());
        assertSame(booking, maybeBooker.get());
    }

    @Test
    void findAllByItemOwner_whenPersist_thenCollectionOfBookingsReturned() {
        List<Booking> maybeBookers = bookingRepository.findAllByItemOwner(
                itemOwner,
                PageRequest.of(0, 10)
        );

        assertFalse(maybeBookers.isEmpty());
        assertEquals(1, maybeBookers.size());

        Booking booking = maybeBookers.get(0);
        assertSame(itemOwner, booking.getItem().getOwner());
    }

    @AfterEach
    public void deleteData() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }
}