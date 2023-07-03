package ru.practicum.shareit.request.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.CreationRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestMapper {
    private final ItemMapper itemMapper;
    public Request toRequest(CreationRequestDto creationRequestDto, User requestor) {
        Request request = new Request();

        request.setDescription(creationRequestDto.getDescription());
        request.setRequestor(requestor);

        return request;
    }

    public RequestDto toRequestDto(Request request) {
        RequestDto requestDto = new RequestDto();

        requestDto.setId(request.getId());
        requestDto.setDescription(request.getDescription());
        requestDto.setCreated(request.getCreated());
        requestDto.setRequestorId(request.getRequestor().getId());
        requestDto.setItems(request.getItems().stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList()));

        return requestDto;
    }
}
