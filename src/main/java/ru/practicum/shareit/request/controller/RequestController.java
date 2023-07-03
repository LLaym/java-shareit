package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreationRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.util.exception.ValidationException;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;
    @PostMapping
    RequestDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                      @RequestBody @Valid CreationRequestDto creationRequestDto) {
        return requestService.create(userId, creationRequestDto);
    }

    @GetMapping
    List<RequestDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getAllByUser(userId);
    }

    @GetMapping("/all")
    List<RequestDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                            @RequestParam(defaultValue = "0") int from,
                            @RequestParam(defaultValue = "10") int size) {
        if (from < 0 || size <= 0) {
            throw new ValidationException("Wrong params");
        }
        return requestService.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    RequestDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                             @PathVariable long requestId) {
        return requestService.getById(userId, requestId);
    }
}
