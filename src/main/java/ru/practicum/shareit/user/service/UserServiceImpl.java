package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.exception.AlreadyExistException;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserDto userDto) {
        throwIfEmailExist(userDto.getEmail());

        User user = userMapper.toUser(userDto);
        User userCreated = userDao.save(user);

        log.info("Добавлен новый пользователь: {}", userCreated);
        return userMapper.toUserDto(userCreated);
    }

    @Override
    public UserDto getById(long id) {
        User user = userDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));

        log.info("Передан пользователь: {}", user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(long id, UserDto userDto) {
        User user = userDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !user.getEmail().equals(userDto.getEmail())) {
            throwIfEmailExist(userDto.getEmail());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        User userUpdated = userDao.save(user);

        log.info("Обновлён пользователь: {}", userUpdated);
        return userMapper.toUserDto(userUpdated);
    }

    @Override
    public void deleteById(long id) {
        User user = userDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));

        userDao.deleteById(id);
        log.info("Удалён пользователь: {}", user);
    }

    @Override
    public List<UserDto> getAll() {
        List<UserDto> usersDtos = userDao.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());

        log.info("Передан список всех пользователей");
        return usersDtos;
    }

    private void throwIfEmailExist(String email) {
        userDao.findByEmail(email)
                .orElseThrow(() -> new AlreadyExistException("Email " + email + " уже существует"));
    }
}
