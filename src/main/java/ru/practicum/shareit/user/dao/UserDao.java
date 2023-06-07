package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

public interface UserDao {
    User save(User user);

    User findById(long id);

    User update(long id, User user);

    void deleteById(long id);
}
