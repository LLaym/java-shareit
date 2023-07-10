package ru.practicum.shareit.integration.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.RequestController;
import ru.practicum.shareit.request.dto.CreationRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
@ExtendWith(MockitoExtension.class)
class RequestControllerIT {

    @Autowired
    ObjectMapper mapper;
    @MockBean
    private RequestService requestService;
    @Autowired
    private MockMvc mvc;

    @Test
    void create_whenCreationRequestDtoValid_thenResponseStatusOkWithRequestDtoInBody() throws Exception {
        long userId = 1L;

        CreationRequestDto creationRequestDto = CreationRequestDto.builder()
                .description("Hammer or something like that")
                .build();

        RequestDto expectedResult = RequestDto.builder()
                .id(1L)
                .description("Hammer or something like that")
                .created("2023-07-05T15:00:00.000000")
                .requestorId(1L)
                .items(Collections.emptyList())
                .build();

        when(requestService.create(anyLong(), any(CreationRequestDto.class))).thenReturn(expectedResult);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(creationRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedResult.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(expectedResult.getDescription())))
                .andExpect(jsonPath("$.created", is(expectedResult.getCreated())))
                .andExpect(jsonPath("$.requestorId", is(expectedResult.getRequestorId()), Long.class))
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    void create_whenCreationRequestDtoDescriptionNull_thenResponseStatusClientError() throws Exception {
        long userId = 1L;

        CreationRequestDto creationRequestDto = CreationRequestDto.builder()
                .build();

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(creationRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        verify(requestService, never()).create(anyLong(), any(CreationRequestDto.class));
    }

    @Test
    void create_whenCreationRequestDtoDescriptionBiggerThan1024_thenResponseStatusClientError() throws Exception {
        long userId = 1L;

        CreationRequestDto creationRequestDto = CreationRequestDto.builder()
                .description("Hammer or something like that hammer or something like that hammer or something like" +
                        " that  hammer or something like that  hammer or something like that  hammer or something " +
                        "like that  hammer or something like that  hammer or something like that  hammer or something" +
                        " like that  hammer or something like that  hammer or something like that  hammer or" +
                        " something like that  hammer or something like that  hammer or something like that  hammer" +
                        " or something like that  hammer or something like that  hammer or something like that" +
                        " hammer or something like that  hammer or something like that  hammer or something like that" +
                        " hammer or something like that hammer or something like that  hammer or something like that " +
                        "hammer or something like that  hammer or something like that  hammer or something like that" +
                        " hammer or something like that  hammer or something like that  hammer or something like" +
                        " that hammer or something like that  hammer or something like that  hammer or something " +
                        "like that  hammer or something like that  hammer or something like ")
                .build();

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(creationRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        verify(requestService, never()).create(anyLong(), any(CreationRequestDto.class));
    }

    @Test
    void getById_whenInvoke_thenResponseStatusOkWithRequestDtoInBody() throws Exception {
        long userId = 1L;
        long requestId = 1L;

        RequestDto expectedResult = RequestDto.builder()
                .id(requestId)
                .description("Hammer or something like that")
                .created("2023-07-05T15:00:00")
                .requestorId(1L)
                .items(Collections.emptyList())
                .build();

        when(requestService.getById(anyLong(), anyLong())).thenReturn(expectedResult);

        mvc.perform(get("/requests/{id}", requestId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedResult.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(expectedResult.getDescription())))
                .andExpect(jsonPath("$.created", is(expectedResult.getCreated())))
                .andExpect(jsonPath("$.requestorId", is(expectedResult.getRequestorId()), Long.class))
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    void getAllByUser_whenInvoke_thenResponseStatusOkWithCollectionOfRequestDtoInBody() throws Exception {
        long userId = 1L;

        RequestDto expectedResult = RequestDto.builder()
                .id(1L)
                .description("Hammer or something like that")
                .created("2023-07-05T15:00:00")
                .requestorId(1L)
                .items(Collections.emptyList())
                .build();

        when(requestService.getAllByUser(anyLong())).thenReturn(List.of(expectedResult));

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(expectedResult.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(expectedResult.getDescription())))
                .andExpect(jsonPath("$[0].created", is(expectedResult.getCreated())))
                .andExpect(jsonPath("$[0].requestorId", is(expectedResult.getRequestorId()), Long.class))
                .andExpect(jsonPath("$[0].items", hasSize(0)));
    }

    @Test
    void getAll_whenFromAndSizeValid_thenResponseStatusOkWithCollectionOfRequestDtoInBody() throws Exception {
        long userId = 1L;

        RequestDto expectedResult = RequestDto.builder()
                .id(1L)
                .description("Hammer or something like that")
                .created("2023-07-05T15:00:00")
                .requestorId(1L)
                .items(Collections.emptyList())
                .build();

        when(requestService.getAll(anyLong(), anyInt(), anyInt())).thenReturn(List.of(expectedResult));

        mvc.perform(get("/requests/all?from=0&size=10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(expectedResult.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(expectedResult.getDescription())))
                .andExpect(jsonPath("$[0].created", is(expectedResult.getCreated())))
                .andExpect(jsonPath("$[0].requestorId", is(expectedResult.getRequestorId()), Long.class))
                .andExpect(jsonPath("$[0].items", hasSize(0)));
    }

    @Test
    void getAll_whenFromOrSizeNotValid_thenResponseStatusServerError() throws Exception {
        long userId = 1L;

        mvc.perform(get("/requests/all?from=-1&size=10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        mvc.perform(get("/requests/all?from=0&size=0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        mvc.perform(get("/requests/all?from=1&size=0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        mvc.perform(get("/requests/all?from=0&size=-1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(requestService, never()).getAllByUser(anyLong());
    }
}