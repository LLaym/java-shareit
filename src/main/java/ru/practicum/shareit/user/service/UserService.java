package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto user);

    UserDto getById(long id);

    UserDto update(long id, UserDto userDto);

    void deleteById(long id);

    List<UserDto> getAll();
}
