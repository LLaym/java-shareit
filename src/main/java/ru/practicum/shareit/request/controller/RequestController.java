package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreationRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    RequestDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                      @RequestBody @Valid CreationRequestDto creationRequestDto) {
        return requestService.create(userId, creationRequestDto);
    }

    @GetMapping("/{requestId}")
    RequestDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                       @PathVariable long requestId) {
        return requestService.getById(userId, requestId);
    }

    @GetMapping
    List<RequestDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getAllByUser(userId);
    }

    @GetMapping("/all")
    List<RequestDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                            @RequestParam(defaultValue = "0", required = false) @Min(0L) int from,
                            @RequestParam(defaultValue = "10", required = false) @Min(1L) int size) {
        return requestService.getAll(userId, from, size);
    }
}
