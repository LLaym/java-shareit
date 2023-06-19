package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.validation.group.AddNewUserAction;
import ru.practicum.shareit.user.validation.group.UpdateUserAction;

import javax.validation.constraints.Min;
import javax.validation.groups.Default;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody @Validated({Default.class, AddNewUserAction.class}) UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userService.create(user));
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable @Min(1L) long id) {
        return UserMapper.toUserDto(userService.getById(id));
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable @Min(1L) long id,
                          @RequestBody @Validated({Default.class, UpdateUserAction.class}) UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userService.update(id, user));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Min(1L) long id) {
        userService.deleteById(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
