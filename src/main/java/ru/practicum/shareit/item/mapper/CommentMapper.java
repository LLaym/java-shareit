package ru.practicum.shareit.item.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreationCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

@Service
public class CommentMapper {
    private static BookingRepository bookingRepository;

    @Autowired
    public CommentMapper(BookingRepository bookingRepository) {
        CommentMapper.bookingRepository = bookingRepository;
    }

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .ownerId(item.getOwner().getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static Comment toComment(CreationCommentDto creationCommentDto) {
        Comment comment = new Comment();
        comment.setText(creationCommentDto.getText());
        return comment;
    }

    public static CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getUser().getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

//    public static ItemExtendedDto toItemExtendedDto(Item item) {
//        ItemExtendedDto itemExtendedDto = new ItemExtendedDto();
//
//        BookingShortDto lastBookingDto = bookingRepository.findFirstByItemOwnerAndEndBeforeOrderByEndDesc(item.getOwner(), LocalDateTime.now())
//                .filter(booking -> booking.getBooker().getId() != item.getOwner().getId())
//                .map(BookingMapper::toBookingShortDto)
//                .orElse(null);
//        BookingShortDto nextBookingDto = bookingRepository.findFirstByItemOwnerAndStartAfterOrderByStartAsc(item.getOwner(), LocalDateTime.now())
//                .filter(booking -> booking.getBooker().getId() != item.getOwner().getId())
//                .map(BookingMapper::toBookingShortDto)
//                .orElse(null);
//
//        itemExtendedDto.setId(item.getId());
//        itemExtendedDto.setOwnerId(item.getOwner().getId());
//        itemExtendedDto.setName(item.getName());
//        itemExtendedDto.setDescription(item.getDescription());
//        itemExtendedDto.setAvailable(item.getAvailable());
//        itemExtendedDto.setLastBooking(lastBookingDto);
//        itemExtendedDto.setNextBooking(nextBookingDto);
//
//        return itemExtendedDto;
//    }
}
