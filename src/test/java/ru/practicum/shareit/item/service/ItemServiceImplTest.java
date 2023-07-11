package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreationCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
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
class ItemServiceImplTest {
    private final EntityManager em;
    private final ItemService itemService;

    private User itemOwner;

    @BeforeEach
    public void addData() {
        itemOwner = new User(null, "John", "john@example.com");
        em.persist(itemOwner);
        em.flush();
    }

    @Test
    void create_whenPerSuccess_thenCreateAndItemDtoReturned() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Hammer");
        itemDto.setDescription("Handy tool");
        itemDto.setAvailable(true);

        ItemDto resultItemDto = itemService.create(itemOwner.getId(), itemDto);

        TypedQuery<Item> query =
                em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item createdItem = query.setParameter("name", itemDto.getName()).getSingleResult();

        assertThat(resultItemDto.getId(), notNullValue());

        assertThat(createdItem.getId(), notNullValue());
        assertThat(createdItem.getOwner().getId(), equalTo(itemOwner.getId()));
        assertThat(createdItem.getName(), equalTo(itemDto.getName()));
        assertThat(createdItem.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(createdItem.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    void getById_whenPersist_thenItemDtoReturned() {
        Item item = new Item();
        item.setOwner(itemOwner);
        item.setName("Hammer");
        item.setDescription("Handy tool");
        item.setAvailable(true);
        em.persist(item);
        em.flush();

        ItemDto resultItemDto = itemService.getById(
                itemOwner.getId(),
                item.getId()
        );

        assertThat(resultItemDto.getId(), notNullValue());
        assertThat(resultItemDto.getOwnerId(), equalTo(itemOwner.getId()));
        assertThat(resultItemDto.getName(), equalTo(item.getName()));
        assertThat(resultItemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(resultItemDto.getAvailable(), equalTo(item.getAvailable()));
    }

    @Test
    void update_whenSuccess_thenUpdateAndItemDtoReturned() {
        Item item = new Item();
        item.setOwner(itemOwner);
        item.setName("Hammer");
        item.setDescription("Handy tool");
        item.setAvailable(true);
        em.persist(item);
        em.flush();

        ItemDto updateItemDto = new ItemDto();
        updateItemDto.setName("Hammer Update");
        updateItemDto.setDescription("Handy tool Update");
        updateItemDto.setAvailable(false);

        ItemDto resultItemDto = itemService.update(
                itemOwner.getId(),
                item.getId(),
                updateItemDto
        );

        TypedQuery<Item> query =
                em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item updatedItem = query.setParameter("name", updateItemDto.getName()).getSingleResult();

        assertThat(resultItemDto.getId(), notNullValue());

        assertThat(updatedItem.getId(), notNullValue());
        assertThat(updatedItem.getOwner().getId(), equalTo(itemOwner.getId()));
        assertThat(updatedItem.getName(), equalTo(updateItemDto.getName()));
        assertThat(updatedItem.getDescription(), equalTo(updateItemDto.getDescription()));
        assertThat(updatedItem.getAvailable(), equalTo(updateItemDto.getAvailable()));
    }

    @Test
    void getAllByOwnerId_whenPersist_thenCollectionOfItemDtoReturned() {
        Item item = new Item();
        item.setOwner(itemOwner);
        item.setName("Hammer");
        item.setDescription("Handy tool");
        item.setAvailable(true);
        em.persist(item);
        em.flush();

        List<ItemDto> resultItemDtos = itemService.getAllByOwnerId(
                itemOwner.getId(),
                0,
                10
        );

        assertThat(resultItemDtos.isEmpty(), is(false));
        assertThat(resultItemDtos.get(0).getId(), notNullValue());
        assertThat(resultItemDtos.get(0).getOwnerId(), equalTo(itemOwner.getId()));
        assertThat(resultItemDtos.get(0).getName(), equalTo(item.getName()));
        assertThat(resultItemDtos.get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(resultItemDtos.get(0).getAvailable(), equalTo(item.getAvailable()));
    }

    @Test
    void getAllBySubstring_whenPersist_thenCollectionOfItemDtoReturned() {
        String search = "hAm";

        Item item = new Item();
        item.setOwner(itemOwner);
        item.setName("Hammer");
        item.setDescription("Handy tool");
        item.setAvailable(true);
        em.persist(item);
        em.flush();

        List<ItemDto> resultItemDtos = itemService.getAllBySubstring(
                itemOwner.getId(),
                search,
                0,
                10
        );

        assertThat(resultItemDtos.isEmpty(), is(false));
        assertThat(resultItemDtos.get(0).getId(), notNullValue());
        assertThat(resultItemDtos.get(0).getOwnerId(), equalTo(itemOwner.getId()));
        assertThat(resultItemDtos.get(0).getName(), equalTo(item.getName()));
        assertThat(resultItemDtos.get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(resultItemDtos.get(0).getAvailable(), equalTo(item.getAvailable()));
    }

    @Test
    void createComment_whenSuccess_thenCreateAndCommentDtoReturned() {
        User booker = new User(null, "Kyle", "kyle@example.com");
        em.persist(booker);
        em.flush();

        Item item = new Item();
        item.setOwner(itemOwner);
        item.setName("Hammer");
        item.setDescription("Handy tool");
        item.setAvailable(true);
        em.persist(item);
        em.flush();

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        em.persist(booking);
        em.flush();

        CreationCommentDto creationCommentDto = new CreationCommentDto();
        creationCommentDto.setText("Hmm, interesting tool...");

        CommentDto resultCommentDto = itemService.createComment(
                booker.getId(),
                item.getId(),
                creationCommentDto
        );

        TypedQuery<Comment> query =
                em.createQuery("Select c from Comment c where c.text = :text", Comment.class);
        Comment createdComment = query.setParameter("text", creationCommentDto.getText()).getSingleResult();

        assertThat(resultCommentDto.getId(), notNullValue());

        assertThat(createdComment.getId(), notNullValue());
        assertThat(createdComment.getText(), equalTo(creationCommentDto.getText()));
        assertThat(createdComment.getItem().getId(), equalTo(item.getId()));
        assertThat(createdComment.getUser().getId(), equalTo(booker.getId()));
        assertThat(createdComment.getCreated(), notNullValue());
    }
}