package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreationRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;
import java.util.Set;

public interface RequestService {
    RequestDto create(long userId, CreationRequestDto creationRequestDto);

    List<RequestDto> getAllByUser(long userId);

    List<RequestDto> getAll(long userId, int from, int size);

    RequestDto getById(long userId, long requestId);
}
