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
    private final UserRepository repository;

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User userCreated = repository.save(user);

        log.info("Added new User: {}", userCreated);
        return UserMapper.toUserDto(userCreated);
    }

    @Override
    public UserDto getById(long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        log.info("Provided User: {}", user);
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto update(long id, UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User userToUpdate = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userToUpdate.setEmail(user.getEmail());
        }

        User userUpdated = repository.save(userToUpdate);

        log.info("Updated User: {}", userUpdated);
        return UserMapper.toUserDto(userUpdated);
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        repository.deleteById(id);
        log.info("Deleted User: {}", user);
    }

    @Override
    public List<UserDto> getAll() {
        List<UserDto> users = repository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());

        log.info("Provided all Users list");
        return users;
    }
}
