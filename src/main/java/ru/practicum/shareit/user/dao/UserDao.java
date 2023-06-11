package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    User save(User user);

    Optional<User> findById(long id);

    User update(User user);

    void deleteById(long id);

    List<User> findAll();

    boolean emailExist(String email);
}
