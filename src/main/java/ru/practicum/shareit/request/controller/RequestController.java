package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreationRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ShortRequestDto;

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
                      @RequestBody CreationRequestDto creationRequestDto) {
        return requestService.create(userId, creationRequestDto);
    }

    @GetMapping
    Set<RequestDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getAllByUser(userId);
    }

    @GetMapping("/all")
    List<ShortRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @RequestParam long from,
                                 @RequestParam int size) {
        return requestService.getAll(userId);
    }

    @GetMapping("/{requestId}")
    List<RequestDto> getById(@RequestHeader("X-Sharer-User-Id") long userId,
                             @PathVariable long requestId) {
        return requestService.getById(userId, requestId);
    }
}
