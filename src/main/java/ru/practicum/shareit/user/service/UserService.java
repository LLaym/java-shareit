package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public interface UserService {
    User addNewUser(User user);

    UserDto getUserById(long id);

    User updateUser(long id, User user);

    void deleteUserById(long id);
}
