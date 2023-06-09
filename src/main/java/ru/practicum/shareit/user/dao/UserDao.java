package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao {
    User save(User user);

    User findById(long id);

    User update(User user);

    void deleteById(long id);

    boolean emailExist(String email);

    List<User> findAll();
}
