package ru.practicum.shareit.integration.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = UserController.class)
class UserControllerIT {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    @Test
    void create_whenUserDtoValid_thenResponseStatusOkWithUserDtoInBody() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("Nick")
                .email("nick2023@gmail.com")
                .build();

        UserDto expectedUserDto = UserDto.builder()
                .id(1L)
                .name("Nick")
                .email("nick2023@gmail.com")
                .build();

        when(userService.create(any(UserDto.class))).thenReturn(expectedUserDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(expectedUserDto.getName())))
                .andExpect(jsonPath("$.email", is(expectedUserDto.getEmail())));
    }

    @Test
    void create_whenUserDtoNameNull_thenResponseStatusClientError() throws Exception {
        UserDto userDto = UserDto.builder()
                .email("nick2023@gmail.com")
                .build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void create_whenUserDtoNameBiggerThan256_thenResponseStatusClientError() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("Nicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknick" +
                        "nicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknick" +
                        "nicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknick")
                .email("nick2023@gmail.com")
                .build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void create_whenUserDtoEmailNull_thenResponseStatusClientError() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("Nick")
                .build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void create_whenUserDtoEmailNotValid_thenResponseStatusClientError() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("Nick")
                .email("nick2023gmail")
                .build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getById_whenInvoke_thenResponseStatusOkWithUserDtoInBody() throws Exception {
        long userId = 1;

        UserDto expectedResult = UserDto.builder()
                .id(userId)
                .name("Nick")
                .email("nick2023@gmail.com")
                .build();

        when(userService.getById(anyLong())).thenReturn(expectedResult);

        mvc.perform(get("/users/{id}", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedResult.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(expectedResult.getName())))
                .andExpect(jsonPath("$.email", is(expectedResult.getEmail())));
    }

    @Test
    void update_whenUserDtoValid_thenResponseStatusOkWithUserDtoInBody() throws Exception {
        long userId = 1;

        UserDto updateBody = UserDto.builder()
                .name("Josh")
                .email("josh123@gmail.com")
                .build();

        UserDto expectedResult = UserDto.builder()
                .id(userId)
                .name("Josh")
                .email("josh123@gmail.com")
                .build();

        when(userService.update(anyLong(), any(UserDto.class))).thenReturn(expectedResult);

        mvc.perform(patch("/users/{id}", userId)
                        .content(mapper.writeValueAsString(updateBody))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedResult.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(expectedResult.getName())))
                .andExpect(jsonPath("$.email", is(expectedResult.getEmail())));
    }

    @Test
    void update_whenUserDtoNameBiggerThan256_thenResponseStatusClienError() throws Exception {
        long userId = 1;

        UserDto updateBody = UserDto.builder()
                .name("Nicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknick" +
                        "nicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknick" +
                        "nicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknicknick")
                .email("nick123@gmail.com")
                .build();

        mvc.perform(patch("/users/{id}", userId)
                        .content(mapper.writeValueAsString(updateBody))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void update_whenUserDtoEmailNotValid_thenResponseStatusClientError() throws Exception {
        long userId = 1;

        UserDto updateBody = UserDto.builder()
                .name("Nick")
                .email("nick123gmail.com")
                .build();

        mvc.perform(patch("/users/{id}", userId)
                        .content(mapper.writeValueAsString(updateBody))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void delete_whenInvoke_thenResponseStatusOk() throws Exception {
        long userId = 1;

        mvc.perform(MockMvcRequestBuilders.delete("/users/{id}", userId)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteById(userId);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getAll_whenInvoke_thenResponseStatusOkWithCollectionOfUserDtoInBody() throws Exception {
        long userId = 1;

        UserDto userDto = UserDto.builder()
                .id(userId)
                .name("Josh")
                .email("josh123@gmail.com")
                .build();

        when(userService.getAll()).thenReturn(List.of(userDto));

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())));
    }
}