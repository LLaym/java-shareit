package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public User add(@RequestBody @Valid User user) {
        return userService.addNewUser(user);
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @PutMapping
    public User update(@RequestBody @Valid User user) {
        return userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        userService.deleteUserById(id);
    }
}
