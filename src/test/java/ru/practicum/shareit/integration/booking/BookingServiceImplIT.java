package ru.practicum.shareit.integration.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreationBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIT {
    private final EntityManager em;
    private final BookingService bookingService;

    private User itemOwner;
    private User booker;
    private Item item;

    @BeforeEach
    public void addData() {
        itemOwner = new User(null, "John", "john@examle.com");
        booker = new User(null, "Danny", "danny@examle.com");
        em.persist(itemOwner);
        em.persist(booker);
        em.flush();

        item = new Item();
        item.setOwner(itemOwner);
        item.setName("Hammer");
        item.setDescription("Handy tool");
        item.setAvailable(true);
        em.persist(item);
        em.flush();
    }

    @Test
    void create_whenSuccess_thenCreateAndBookingDtoReturned() {
        CreationBookingDto creationBookingDto = new CreationBookingDto();
        creationBookingDto.setItemId(item.getId());
        creationBookingDto.setStart(LocalDateTime.now().plusDays(1).toString());
        creationBookingDto.setEnd(LocalDateTime.now().plusDays(2).toString());

        BookingDto resultBookingDto = bookingService.create(booker.getId(), creationBookingDto);

        TypedQuery<Booking> query =
                em.createQuery("Select b from Booking b where b.booker.name = :name", Booking.class);
        Booking createdBooking = query.setParameter("name", booker.getName()).getSingleResult();

        assertThat(resultBookingDto.getId(), notNullValue());

        assertThat(createdBooking.getId(), notNullValue());
        assertThat(createdBooking.getStart(), notNullValue());
        assertThat(createdBooking.getEnd(), notNullValue());
        assertThat(createdBooking.getItem().getId(), notNullValue());
        assertThat(createdBooking.getItem().getName(), equalTo(item.getName()));
        assertThat(createdBooking.getItem().getDescription(), equalTo(item.getDescription()));
        assertThat(createdBooking.getItem().getAvailable(), equalTo(item.getAvailable()));
        assertThat(createdBooking.getBooker().getId(), notNullValue());
        assertThat(createdBooking.getBooker().getName(), equalTo(booker.getName()));
        assertThat(createdBooking.getBooker().getEmail(), equalTo(booker.getEmail()));
        assertThat(createdBooking.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void confirmStatus_whenSuccess_thenConfirmAndBookingDtoReturned() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);
        em.flush();

        BookingDto resultBookingDto = bookingService.confirmStatus(
                itemOwner.getId(),
                booking.getId(),
                "true"
        );

        TypedQuery<Booking> query =
                em.createQuery("Select b from Booking b where b.booker.name = :name", Booking.class);
        Booking createdBooking = query.setParameter("name", booker.getName()).getSingleResult();

        assertThat(resultBookingDto.getId(), notNullValue());

        assertThat(createdBooking.getId(), notNullValue());
        assertThat(createdBooking.getStart(), notNullValue());
        assertThat(createdBooking.getEnd(), notNullValue());
        assertThat(createdBooking.getItem().getId(), notNullValue());
        assertThat(createdBooking.getItem().getName(), equalTo(item.getName()));
        assertThat(createdBooking.getItem().getDescription(), equalTo(item.getDescription()));
        assertThat(createdBooking.getItem().getAvailable(), equalTo(item.getAvailable()));
        assertThat(createdBooking.getBooker().getId(), notNullValue());
        assertThat(createdBooking.getBooker().getName(), equalTo(booker.getName()));
        assertThat(createdBooking.getBooker().getEmail(), equalTo(booker.getEmail()));
        assertThat(createdBooking.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void getById_whenPersist_thenBookingDtoReturned() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);
        em.flush();

        BookingDto resultBookingDto = bookingService.getById(
                booker.getId(),
                booking.getId()
        );

        assertThat(resultBookingDto.getId(), notNullValue());
        assertThat(resultBookingDto.getItem().getId(), notNullValue());
        assertThat(resultBookingDto.getItem().getName(), equalTo(item.getName()));
        assertThat(resultBookingDto.getItem().getDescription(), equalTo(item.getDescription()));
        assertThat(resultBookingDto.getItem().getAvailable(), equalTo(item.getAvailable()));
        assertThat(resultBookingDto.getBooker().getId(), notNullValue());
        assertThat(resultBookingDto.getBooker().getName(), equalTo(booker.getName()));
        assertThat(resultBookingDto.getBooker().getEmail(), equalTo(booker.getEmail()));
        assertThat(resultBookingDto.getStatus(), equalTo("WAITING"));
    }

    @Test
    void getAllByUser_whenPersist_thenCollectionOfBookingDtoReturned() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);
        em.flush();

        List<BookingDto> resultBookingDto = bookingService.getAllByUser(
                booker.getId(),
                "FUTURE",
                0,
                10
        );

        assertThat(resultBookingDto.isEmpty(), is(false));
        assertThat(resultBookingDto.get(0).getId(), notNullValue());
        assertThat(resultBookingDto.get(0).getItem().getId(), notNullValue());
        assertThat(resultBookingDto.get(0).getItem().getName(), equalTo(item.getName()));
        assertThat(resultBookingDto.get(0).getItem().getDescription(), equalTo(item.getDescription()));
        assertThat(resultBookingDto.get(0).getItem().getAvailable(), equalTo(item.getAvailable()));
        assertThat(resultBookingDto.get(0).getBooker().getId(), notNullValue());
        assertThat(resultBookingDto.get(0).getBooker().getName(), equalTo(booker.getName()));
        assertThat(resultBookingDto.get(0).getBooker().getEmail(), equalTo(booker.getEmail()));
        assertThat(resultBookingDto.get(0).getStatus(), equalTo("WAITING"));
    }

    @Test
    void getAllByItemOwner_whenPersist_thenCollectionOfBookingDtoReturned() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);
        em.flush();

        List<BookingDto> resultBookingDto = bookingService.getAllByItemOwner(
                itemOwner.getId(),
                "FUTURE",
                0,
                10
        );

        assertThat(resultBookingDto.isEmpty(), is(false));
        assertThat(resultBookingDto.get(0).getId(), notNullValue());
        assertThat(resultBookingDto.get(0).getItem().getId(), notNullValue());
        assertThat(resultBookingDto.get(0).getItem().getName(), equalTo(item.getName()));
        assertThat(resultBookingDto.get(0).getItem().getDescription(), equalTo(item.getDescription()));
        assertThat(resultBookingDto.get(0).getItem().getAvailable(), equalTo(item.getAvailable()));
        assertThat(resultBookingDto.get(0).getBooker().getId(), notNullValue());
        assertThat(resultBookingDto.get(0).getBooker().getName(), equalTo(booker.getName()));
        assertThat(resultBookingDto.get(0).getBooker().getEmail(), equalTo(booker.getEmail()));
        assertThat(resultBookingDto.get(0).getStatus(), equalTo("WAITING"));
    }
}