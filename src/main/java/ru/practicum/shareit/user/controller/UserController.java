package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.validation.group.AddNewUserAction;
import ru.practicum.shareit.user.validation.group.UpdateUserAction;

import javax.validation.constraints.Min;
import javax.validation.groups.Default;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto addNewUser(@RequestBody @Validated({Default.class, AddNewUserAction.class}) UserDto userDto) {
        return userService.addNewUser(userDto);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable @Min(1L) long id) {
        return userService.getUserById(id);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable @Min(1L) long id,
                              @RequestBody @Validated({Default.class, UpdateUserAction.class}) UserDto userDto) {
        return userService.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable @Min(1L) long id) {
        userService.deleteUserById(id);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }
}
