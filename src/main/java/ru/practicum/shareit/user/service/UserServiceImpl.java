package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final UserMapper userMapper;

    @Override
    public User addNewUser(User user) {
        return userDao.save(user);
    }

    @Override
    public UserDto getUserById(long id) {
        User user = userDao.findById(id);
        return userMapper.toUserDto(user);
    }

    @Override
    public User updateUser(long id, User user) {
        return userDao.update(id, user);
    }

    @Override
    public void deleteUserById(long id) {
        userDao.deleteById(id);
    }
}
