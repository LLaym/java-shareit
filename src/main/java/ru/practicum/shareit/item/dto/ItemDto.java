package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.validation.group.AddNewItemAction;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {
    private Long id;
    private Long ownerId;

    @NotNull(groups = AddNewItemAction.class, message = "Name should not be null")
    @Size(min = 1, max = 256, message = "Name length should not be longer than 256 characters")
    private String name;

    @NotNull(groups = AddNewItemAction.class, message = "Description should not be null")
    @Size(min = 1, max = 512, message = "Description length should not be longer than 512 characters")
    private String description;

    @NotNull(groups = AddNewItemAction.class, message = "Sharing availability should not be null")
    private Boolean available;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private Long requestId;
    private List<CommentDto> comments = new ArrayList<>();
}
