package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private User owner;
    private Item item;

    @BeforeEach
    public void addData() {
        owner = new User(null, "John", "john@example.com");

        userRepository.save(owner);

        item = new Item();
        item.setOwner(owner);
        item.setName("Hammer");
        item.setDescription("Handy tool");
        item.setAvailable(false);

        itemRepository.save(item);
    }

    @Test
    void findAllByOwner_whenPersist_thenCollectionOfItemsReturned() {
        List<Item> maybeItems = itemRepository.findAllByOwner(
                owner,
                PageRequest.of(0, 10)
        );

        assertFalse(maybeItems.isEmpty());
        assertEquals(1, maybeItems.size());

        Item actualItem = maybeItems.get(0);
        assertSame(item, actualItem);
    }

    @Test
    void findAllByNameOrDescriptionContainingIgnoreCase_whenPersist_thenCollectionOfItemsReturned() {
        String search = "hAm";

        List<Item> maybeItems = itemRepository.findBySearchTermIgnoreCase(
                search,
                PageRequest.of(0, 10)
        );

        assertFalse(maybeItems.isEmpty());
        assertEquals(1, maybeItems.size());

        Item actualItem = maybeItems.get(0);
        assertSame(item, actualItem);
    }
}