package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.exception.AlreadyExistException;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Override
    public User create(User user) {
        throwIfEmailExist(user.getEmail());

        User userCreated = userDao.save(user);

        log.info("Добавлен новый пользователь: {}", userCreated);
        return userCreated;
    }

    @Override
    public User getById(long id) {
        User user = userDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));

        log.info("Передан пользователь: {}", user);
        return user;
    }

    @Override
    public User update(long id, User user) {
        User userToUpdate = userDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));

        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        if (user.getEmail() != null && !userToUpdate.getEmail().equals(user.getEmail())) {
            throwIfEmailExist(user.getEmail());
        }
        if (user.getEmail() != null) {
            userToUpdate.setEmail(user.getEmail());
        }

        User userUpdated = userDao.save(userToUpdate);

        log.info("Обновлён пользователь: {}", userUpdated);
        return userUpdated;
    }

    @Override
    public void deleteById(long id) {
        User user = userDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));

        userDao.deleteById(id);
        log.info("Удалён пользователь: {}", user);
    }

    @Override
    public List<User> getAll() {
        List<User> users = userDao.findAll();

        log.info("Передан список всех пользователей");
        return users;
    }

    private void throwIfEmailExist(String email) {
        if (userDao.findByEmail(email).isPresent()) {
            throw new AlreadyExistException("Email " + email + " уже существует");
        }
    }
}
