package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto addNewUser(UserDto userDto);

    UserDto getUserById(long id);

    UserDto updateUser(long id, UserDto userDto);

    void deleteUserById(long id);
}
