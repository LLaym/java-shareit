package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreationRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto create(long userId, CreationRequestDto creationRequestDto);

    RequestDto getById(long userId, long requestId);

    List<RequestDto> getAllByUser(long userId);

    List<RequestDto> getAll(long userId, int from, int size);
}
