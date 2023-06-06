package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryUserDao implements UserDao {
    private final Map<Long, User> users = new HashMap<>();
    private Long nextId = 0L;

    @Override
    public User save(User user) {
        user.setId(nextId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(long id) {
        return users.get(id);
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteById(long id) {
        users.remove(id);
    }
}
