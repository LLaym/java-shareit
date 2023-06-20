package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.exception.AlreadyExistException;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public User create(User user) {
//        throwIfEmailExist(user.getEmail());
        User userCreated = repository.save(user);

        log.info("Добавлен новый пользователь: {}", userCreated);
        return userCreated;
    }

    @Override
    public User getById(long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));

        log.info("Передан пользователь: {}", user);
        return user;
    }

    @Override
    public User update(long id, User user) {
        User userToUpdate = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));

        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
//        if (user.getEmail() != null && !userToUpdate.getEmail().equals(user.getEmail())) {
//            throwIfEmailExist(user.getEmail());
//        }
        if (user.getEmail() != null) {
            userToUpdate.setEmail(user.getEmail());
        }

        User userUpdated = repository.save(userToUpdate);

        log.info("Обновлён пользователь: {}", userUpdated);
        return userUpdated;
    }

    @Override
    public void deleteById(long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));

        repository.deleteById(id);
        log.info("Удалён пользователь: {}", user);
    }

    @Override
    public List<User> getAll() {
        List<User> users = repository.findAll();

        log.info("Передан список всех пользователей");
        return users;
    }

//    private void throwIfEmailExist(String email) {
//        if (repository.findByEmail(email).isPresent()) {
//            throw new AlreadyExistException("Email " + email + " уже существует");
//        }
//    }
}
