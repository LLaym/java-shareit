package ru.practicum.shareit.unit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void create_thenReturnedUserDto() {
        UserDto userDto = new UserDto(1L, "John", "john@example.com");
        User user = new User(1L, "John", "john@example.com");
        UserDto expectedUserDto = new UserDto(1L, "John", "john@example.com");

        when(userMapper.toUser(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(expectedUserDto);

        UserDto createdUserDto = userService.create(userDto);

        verify(userMapper).toUser(userDto);
        verify(userRepository).save(user);
        verify(userMapper).toUserDto(user);
        assertEquals(expectedUserDto, createdUserDto);
    }

    @Test
    void getById_whenUserFound_thenReturnedUserDto() {
        Long userId = 1L;
        User user = new User(userId, "John", "john@example.com");
        UserDto expectedUserDto = new UserDto(userId, "John", "john@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(expectedUserDto);

        UserDto result = userService.getById(userId);

        verify(userRepository).findById(userId);
        verify(userMapper).toUserDto(user);
        assertEquals(expectedUserDto, result);
    }

    @Test
    void getById_whenUserNotFound_thenNotFoundExceptionThrown() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(userId));
        verify(userRepository).findById(userId);
    }

    @Test
    void update_whenUserFound_thenUpdatedUser() {
        long userId = 1L;
        String newName = "John Doe";
        String newEmail = "john.doe@example.com";

        UserDto userDto = new UserDto(null, newName, newEmail);
        User user = new User(null, newName, newEmail);

        when(userMapper.toUser(userDto)).thenReturn(user);

        User userToUpdate = new User(userId, "Old Name", "old.email@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(userToUpdate));

        User updatedUser = new User(userId, newName, newEmail);

        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto updatedUserDto = new UserDto(userId, newName, newEmail);

        when(userMapper.toUserDto(updatedUser)).thenReturn(updatedUserDto);

        UserDto result = userService.update(userId, userDto);

        verify(userRepository).findById(userId);
        verify(userRepository).save(userToUpdate);
        verify(userMapper).toUserDto(updatedUser);

        assertEquals(newName, result.getName());
        assertEquals(newEmail, result.getEmail());
        assertEquals(updatedUserDto, result);
    }

    @Test
    void update_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 999L;
        UserDto userDto = new UserDto(null, "John Doe", "john.doe@example.com");

        User user = new User(null, "John Doe", "john.doe@example.com");

        when(userMapper.toUser(userDto)).thenReturn(user);
        when(userRepository.findById(userId)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> userService.update(userId, userDto));
        verify(userMapper, times(1)).toUser(userDto);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
        verifyNoMoreInteractions(userMapper, userRepository);
    }

    @Test
    void deleteById_whenUserFound_thenDeleteUser() {
        Long userId = 1L;
        User user = new User(userId, "John", "john@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteById(1L);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).deleteById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteById_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.deleteById(userId));
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAll_whenInvoke_thenReturnedCollectionOfUserDto() {
        User user1 = new User(1L, "John", "john@example.com");
        User user2 = new User(2L, "Kyle", "john@example.com");
        List<User> userList = Arrays.asList(user1, user2);

        UserDto userDto1 = new UserDto(1L, "John", "john@example.com");
        UserDto userDto2 = new UserDto(2L, "Kyle", "john@example.com");
        List<UserDto> expectedUserDtoList = Arrays.asList(userDto1, userDto2);

        when(userRepository.findAll()).thenReturn(userList);
        when(userMapper.toUserDto(user1)).thenReturn(userDto1);
        when(userMapper.toUserDto(user2)).thenReturn(userDto2);

        List<UserDto> actualUserDtoList = userService.getAll();

        assertEquals(expectedUserDtoList.size(), actualUserDtoList.size());
        for (int i = 0; i < expectedUserDtoList.size(); i++) {
            UserDto expectedUserDto = expectedUserDtoList.get(i);
            UserDto actualUserDto = actualUserDtoList.get(i);
            assertEquals(expectedUserDto, actualUserDto);
        }
    }
}