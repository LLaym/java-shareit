package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        User userCreated = userRepository.save(user);

        log.info("Added new User: {}", userCreated);
        return userMapper.toUserDto(userCreated);
    }

    @Override
    public UserDto getById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        log.info("Provided User: {}", user);
        return userMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto update(long id, UserDto userDto) {
        User user = userMapper.toUser(userDto);
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        updateFields(user, userToUpdate);

        User userUpdated = userRepository.save(userToUpdate);

        log.info("Updated User: {}", userUpdated);
        return userMapper.toUserDto(userUpdated);
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        userRepository.deleteById(id);
        log.info("Deleted User: {}", user);
    }

    @Override
    public List<UserDto> getAll() {
        List<UserDto> users = userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());

        log.info("Provided all Users list");
        return users;
    }

    private void updateFields(User user, User userToUpdate) {
        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userToUpdate.setEmail(user.getEmail());
        }
    }
}
