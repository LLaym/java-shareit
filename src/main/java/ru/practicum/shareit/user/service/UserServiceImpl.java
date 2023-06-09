package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.EmailAlreadyExistException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final UserMapper userMapper;

    @Override
    public UserDto addNewUser(UserDto userDto) {
        checkEmailDuplicate(userDto.getEmail());

        User user = userMapper.toUser(userDto);
        User userCreated = userDao.save(user);

        return userMapper.toUserDto(userCreated);
    }

    @Override
    public UserDto getUserById(long id) {
        checkUserExist(id);

        User user = userDao.findById(id);

        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(long id, UserDto userDto) {
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

        return userMapper.toUserDto(userDao.update(user));
    }

    @Override
    public void deleteUserById(long id) {
        checkUserExist(id);
        userDao.deleteById(id);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userDao.findAll();
        List<UserDto> usersDtos = new ArrayList<>();

        for (User user : users) {
            usersDtos.add(userMapper.toUserDto(user));
        }

        return usersDtos;
    }

    private void checkEmailDuplicate(String email) {
        if (userDao.emailExist(email)) {
            throw new EmailAlreadyExistException("Такой email уже существует");
        }
    }

    private void checkUserExist(long id) {
        if (!userDao.userExist(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
    }
}
