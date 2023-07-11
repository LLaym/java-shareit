package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.validation.group.AddNewUserAction;
import ru.practicum.shareit.user.validation.group.UpdateUserAction;

import javax.validation.groups.Default;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody @Validated({Default.class, AddNewUserAction.class}) UserDto userDto) {
        return userService.create(userDto);
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable long id) {
        return userService.getById(id);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable long id,
                          @RequestBody @Validated({Default.class, UpdateUserAction.class}) UserDto userDto) {
        return userService.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        userService.deleteById(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll();
    }
}
