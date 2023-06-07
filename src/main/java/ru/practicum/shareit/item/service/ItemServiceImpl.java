package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto addNewItem(long ownerId, ItemDto itemDto) {
        Item item = new Item();

        item.setOwnerId(ownerId);
        itemDao.save(item);

        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItemById(long id) {
        Item item = itemDao.findById(id);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto) {
        Item item = new Item();

        itemDao.update(item);

        return itemMapper.toItemDto(item);
    }

    @Override
    public void deleteItemById(long id) {
        itemDao.deleteById(id);
    }
}
