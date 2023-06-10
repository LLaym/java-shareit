package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.exception.AlreadyExistException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserDto userDto) {
        checkEmailDuplicate(userDto.getEmail());

        User user = userMapper.toUser(userDto);
        User userCreated = userDao.save(user);

        log.info("Добавлен новый пользователь: {}", userCreated);
        return userMapper.toUserDto(userCreated);
    }

    @Override
    public UserDto getById(long id) {
        checkUserExist(id);

        User user = userDao.findById(id);

        log.info("Передан пользователь: {}", user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(long id, UserDto userDto) {
        checkUserExist(id);

        User user = userDao.findById(id);

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !user.getEmail().equals(userDto.getEmail())) {
            checkEmailDuplicate(userDto.getEmail());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        User userUpdated = userDao.update(user);

        log.info("Обновлён пользователь: {}", userUpdated);
        return userMapper.toUserDto(userUpdated);
    }

    @Override
    public void deleteById(long id) {
        checkUserExist(id);

        User user = userDao.findById(id);

        userDao.deleteById(id);
        log.info("Удалён пользователь: {}", user);
    }

    @Override
    public List<UserDto> getAll() {
        List<User> users = userDao.findAll();
        List<UserDto> usersDtos = new ArrayList<>();

        for (User user : users) {
            usersDtos.add(userMapper.toUserDto(user));
        }

        log.info("Передан список пользователей: {}", users);
        return usersDtos;
    }

    private void checkEmailDuplicate(String email) {
        if (userDao.emailExist(email)) {
            throw new AlreadyExistException("Такой email уже существует");
        }
    }

    private void checkUserExist(long id) {
        if (!userDao.userExist(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
    }
}
