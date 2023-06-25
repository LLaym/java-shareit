package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User userCreated = repository.save(user);

        log.info("Добавлен новый пользователь: {}", userCreated);
        return UserMapper.toUserDto(userCreated);
    }

    @Override
    public UserDto getById(long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));

        log.info("Передан пользователь: {}", user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(long id, UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User userToUpdate = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));

        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userToUpdate.setEmail(user.getEmail());
        }

        User userUpdated = repository.save(userToUpdate);

        log.info("Обновлён пользователь: {}", userUpdated);
        return UserMapper.toUserDto(userUpdated);
    }

    @Override
    public void deleteById(long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));

        repository.deleteById(id);
        log.info("Удалён пользователь: {}", user);
    }

    @Override
    public List<UserDto> getAll() {
        List<UserDto> users = repository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());

        log.info("Передан список всех пользователей");
        return users;
    }
}
