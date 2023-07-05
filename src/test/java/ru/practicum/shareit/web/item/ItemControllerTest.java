package ru.practicum.shareit.web.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreationCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    @Test
    void create_whenItemDtoValid_thenResponseStatusOkWithItemDtoInBody() throws Exception {
        ItemDto expectedItemDto = ItemDto.builder()
                .id(1L)
                .ownerId(1L)
                .name("Hammer")
                .description("Useful tool")
                .available(true)
                .lastBooking(null)
                .nextBooking(null)
                .requestId(1L)
                .comments(Collections.emptyList())
                .build();

        ItemDto inputItemDto = ItemDto.builder()
                .name("Hammer")
                .description("Useful tool")
                .available(true)
                .build();

        when(itemService.create(anyLong(), any(ItemDto.class))).thenReturn(expectedItemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(inputItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.ownerId", is(expectedItemDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.name", is(expectedItemDto.getName())))
                .andExpect(jsonPath("$.description", is(expectedItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(expectedItemDto.getAvailable())))
                .andExpect(jsonPath("$.lastBooking", is(expectedItemDto.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(expectedItemDto.getNextBooking())))
                .andExpect(jsonPath("$.requestId", is(expectedItemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.comments", hasSize(0)));
    }

    @Test
    void create_whenItemDtoNameNull_thenResponseStatusClientError() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .description("Useful tool")
                .available(true)
                .build();

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void create_whenItemDtoNameSizeBiggerThen256_thenResponseStatusClientError() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .name("Haaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaammmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm" +
                        "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmeeeeeeeeeee" +
                        "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeerrrrrrrrrrrrrrrrrrrrrrrrrr")
                .description("Useful tool")
                .available(true)
                .build();

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void create_whenItemDtoDescriptionNull_thenResponseStatusClientError() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .name("Hammer")
                .available(true)
                .build();

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void create_whenItemDtoDescriptionSizeBiggerThen512_thenResponseStatusClientError() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .name("Hammer")
                .description("Really useful tool really useful tool really useful tool really useful tool really" +
                        " useful tool really useful tool really useful tool really useful tool really useful tool" +
                        " really useful tool really useful tool really useful tool really useful tool really useful" +
                        " tool really useful tool really useful tool really useful tool really useful tool really" +
                        " useful tool really useful tool really useful tool really useful tool really useful tool" +
                        " really useful tool really useful tool really useful tool really useful tool really" +
                        " useful tool")
                .available(true)
                .build();

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void create_whenItemDtoAvailableNull_thenResponseStatusClientError() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .name("Hammer")
                .description("Useful tool")
                .build();

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getById_whenInvoke_thenResponseStatusOkWithItemDtoInBody() throws Exception {
        ItemDto expectedItemDto = ItemDto.builder()
                .id(1L)
                .ownerId(1L)
                .name("Hammer")
                .description("Useful tool")
                .available(true)
                .lastBooking(null)
                .nextBooking(null)
                .requestId(1L)
                .comments(Collections.emptyList())
                .build();

        when(itemService.getById(anyLong(), anyLong())).thenReturn(expectedItemDto);

        mvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.ownerId", is(expectedItemDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.name", is(expectedItemDto.getName())))
                .andExpect(jsonPath("$.description", is(expectedItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(expectedItemDto.getAvailable())))
                .andExpect(jsonPath("$.lastBooking", is(expectedItemDto.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(expectedItemDto.getNextBooking())))
                .andExpect(jsonPath("$.requestId", is(expectedItemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.comments", hasSize(0)));
    }

    @Test
    void update_whenItemDtoValid_thenResponseStatusOkWithItemDtoInBody() throws Exception {
        ItemDto updateBody = ItemDto.builder()
                .name("Heavy hammer")
                .build();

        ItemDto expectedItemDto = ItemDto.builder()
                .id(1L)
                .ownerId(1L)
                .name("Heavy hammer")
                .description("Useful tool")
                .available(true)
                .lastBooking(null)
                .nextBooking(null)
                .requestId(1L)
                .comments(Collections.emptyList())
                .build();

        when(itemService.update(anyLong(), anyLong(), any(ItemDto.class))).thenReturn(expectedItemDto);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(updateBody))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.ownerId", is(expectedItemDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.name", is(expectedItemDto.getName())))
                .andExpect(jsonPath("$.description", is(expectedItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(expectedItemDto.getAvailable())))
                .andExpect(jsonPath("$.lastBooking", is(expectedItemDto.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(expectedItemDto.getNextBooking())))
                .andExpect(jsonPath("$.requestId", is(expectedItemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.comments", hasSize(0)));
    }

    @Test
    void update_whenIdLessThanZero_thenResponseStatusServerError() throws Exception {
        mvc.perform(patch("/items/-1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void getAllByOwner_whenFromAndSizeValid_thenResponseStatusOkWithItemDtoCollectionInBody() throws Exception {
        ItemDto expectedItemDto = ItemDto.builder()
                .id(1L)
                .ownerId(1L)
                .name("Heavy hammer")
                .description("Useful tool")
                .available(true)
                .lastBooking(null)
                .nextBooking(null)
                .requestId(1L)
                .comments(Collections.emptyList())
                .build();

        when(itemService.getAllByOwnerId(anyLong(), anyInt(), anyInt())).thenReturn(List.of(expectedItemDto));

        mvc.perform(get("/items?from=0&size=10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(expectedItemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].ownerId", is(expectedItemDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(expectedItemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(expectedItemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(expectedItemDto.getAvailable())))
                .andExpect(jsonPath("$[0].lastBooking", is(expectedItemDto.getLastBooking())))
                .andExpect(jsonPath("$[0].nextBooking", is(expectedItemDto.getNextBooking())))
                .andExpect(jsonPath("$[0].requestId", is(expectedItemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$[0].comments", hasSize(0)));
    }

    @Test
    void getAllByOwner_whenFromOrSizeNotValid_thenResponseStatusServerError() throws Exception {
        mvc.perform(get("/items?from=-1&size=10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        mvc.perform(get("/items?from=0&size=0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        mvc.perform(get("/items?from=1&size=0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        mvc.perform(get("/items?from=0&size=-1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void getAllBySubstring_whenFromAndSizeValid_thenResponseStatusOkWithItemDtoCollectionInBody() throws Exception {
        ItemDto expectedItemDto = ItemDto.builder()
                .id(1L)
                .ownerId(1L)
                .name("Heavy hammer")
                .description("Useful tool")
                .available(true)
                .lastBooking(null)
                .nextBooking(null)
                .requestId(1L)
                .comments(Collections.emptyList())
                .build();

        when(itemService.getAllBySubstring(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(expectedItemDto));

        mvc.perform(get("/items/search?text=hAmm&from=0&size=10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(expectedItemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].ownerId", is(expectedItemDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(expectedItemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(expectedItemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(expectedItemDto.getAvailable())))
                .andExpect(jsonPath("$[0].lastBooking", is(expectedItemDto.getLastBooking())))
                .andExpect(jsonPath("$[0].nextBooking", is(expectedItemDto.getNextBooking())))
                .andExpect(jsonPath("$[0].requestId", is(expectedItemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$[0].comments", hasSize(0)));
    }

    @Test
    void getAllBySubstring_whenFromOrSizeNotValid_thenResponseStatusServerError() throws Exception {
        mvc.perform(get("/items/search?text=hAmm&from=-1&size=10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        mvc.perform(get("/items/search?text=hAmm&from=0&size=0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        mvc.perform(get("/items/search?text=hAmm&from=1&size=0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        mvc.perform(get("/items/search?text=hAmm&from=0&size=-1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void createComment_whenCreationCommentDtoValid_thenResponseStatusOkWithItemDtoInBody() throws Exception {
        CreationCommentDto creationCommentDto = CreationCommentDto.builder()
                .text("Its too heavy...")
                .build();

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Its too heavy...")
                .authorName("Josh")
                .created("2023-07-05T15:00:00.000000")
                .build();

        when(itemService.createComment(anyLong(), anyLong(), any(CreationCommentDto.class))).thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(creationCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated())));
    }

    @Test
    void createComment_whenCreationCommentDtoTextNull_thenResponseStatusClientError() throws Exception {
        CreationCommentDto creationCommentDto = CreationCommentDto.builder()
                .build();

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(creationCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createComment_whenCreationCommentDtoTextSizeBiggerThen1024_thenResponseStatusClientError() throws Exception {
        CreationCommentDto creationCommentDto = CreationCommentDto.builder()
                .text("Its too heavy, really heavy, really heavy really heavy, really heavy really heavy, really" +
                        " heavy really heavy, really heavy really heavy, really heavy really heavy, really heavy" +
                        " really heavy, really heavy really heavy, really heavy really heavy, really heavy really" +
                        " heavy, really heavy really heavy, really heavy really heavy, really heavy really heavy," +
                        " really heavy really heavy, really heavy really heavy, really heavy really heavy, really" +
                        " heavy really heavy, really heavy really heavy, really heavy really heavy, really " +
                        " really heavy, really heavy really heavy, really heavy really heavy, really heavy really" +
                        " heavy, really heavy really heavy, really heavy really heavy, really heavy really heavy," +
                        " really heavy really heavy, really heavy really heavy, really heavy really heavy, really" +
                        " heavy really heavy, really heavy really heavy, really heavy really heavy, really heavy" +
                        " really heavy, really heavy really heavy, really heavy really heavy, really heavy really" +
                        " heavy, really heavy really heavy, really heavy really heavy, really heavy ")
                .build();

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(creationCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }
}