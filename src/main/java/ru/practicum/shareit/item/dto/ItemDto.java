package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.validation.group.AddNewItemAction;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    private Long ownerId;

    @NotNull(groups = AddNewItemAction.class, message = "Название не может быть пустым")
    @Size(min = 1, max = 64, message = "Название не может быть длинее 64 символов")
    private String name;

    @NotNull(groups = AddNewItemAction.class, message = "Описание не может быть пустым")
    @Size(min = 1, max = 256, message = "Описание не может быть длинее 256 символов")
    private String description;

    @NotNull(groups = AddNewItemAction.class, message = "Доступность для шеринга не может быть пустой")
    private Boolean available;
}
