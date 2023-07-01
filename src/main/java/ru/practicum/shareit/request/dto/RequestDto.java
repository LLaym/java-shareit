package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.request.model.RequestAnswer;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Getter @Setter
public class RequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private User requestor;
    private List<RequestAnswer> answers;
}
