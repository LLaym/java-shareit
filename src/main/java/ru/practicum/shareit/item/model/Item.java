package ru.practicum.shareit.item.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    private Long id;
    @Positive
    private Long ownerId;
    @NotBlank
    private String name;
    private String description;
    @NotNull
    private Boolean isAvailable;
}
