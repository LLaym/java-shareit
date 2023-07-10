package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreationBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;

    @Test
    void create_whenCreationBookingDtoValid_thenResponseStatusOkWithBookingDtoInBody() throws Exception {
        long userId = 1L;

        CreationBookingDto creationBookingDto = CreationBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(1).toString())
                .end(LocalDateTime.now().plusDays(1).toString())
                .build();

        BookingDto expectedResult = BookingDto.builder()
                .id(1L)
                .start("2023-07-05T15:00:00.000000")
                .end("2023-07-06T20:00:00.000000")
                .item(null)
                .booker(null)
                .status("WAITING")
                .build();

        when(bookingService.create(anyLong(), any(CreationBookingDto.class))).thenReturn(expectedResult);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(creationBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedResult.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(expectedResult.getStart())))
                .andExpect(jsonPath("$.end", is(expectedResult.getEnd())))
                .andExpect(jsonPath("$.item", is(nullValue(ItemDto.class))))
                .andExpect(jsonPath("$.booker", is(nullValue(UserDto.class))))
                .andExpect(jsonPath("$.status", is(expectedResult.getStatus())));
    }

    @Test
    void create_whenCreationBookingDtoItemIdLessThenZero_thenResponseStatusClientError() throws Exception {
        long userId = 1L;

        CreationBookingDto creationBookingDto = CreationBookingDto.builder()
                .itemId(-1L)
                .start(LocalDateTime.now().plusHours(1).toString())
                .end(LocalDateTime.now().plusDays(1).toString())
                .build();

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(creationBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        verify(bookingService, never()).create(anyLong(), any(CreationBookingDto.class));
    }

    @Test
    void create_whenCreationBookingDtoItemIdNull_thenResponseStatusClientError() throws Exception {
        long userId = 1L;

        CreationBookingDto creationBookingDto = CreationBookingDto.builder()
                .start(LocalDateTime.now().plusHours(1).toString())
                .end(LocalDateTime.now().plusDays(1).toString())
                .build();

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(creationBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        verify(bookingService, never()).create(anyLong(), any(CreationBookingDto.class));
    }

    @Test
    void create_whenCreationBookingDtoStartNull_thenResponseStatusClientError() throws Exception {
        long userId = 1L;

        CreationBookingDto creationBookingDto = CreationBookingDto.builder()
                .itemId(1L)
                .end(LocalDateTime.now().plusDays(1).toString())
                .build();

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(creationBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        verify(bookingService, never()).create(anyLong(), any(CreationBookingDto.class));
    }

    @Test
    void create_whenCreationBookingDtoEndNull_thenResponseStatusClientError() throws Exception {
        long userId = 1L;

        CreationBookingDto creationBookingDto = CreationBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(1).toString())
                .build();

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(creationBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        verify(bookingService, never()).create(anyLong(), any(CreationBookingDto.class));
    }

    @Test
    void create_whenCreationBookingDtoEndBeforeNow_thenResponseStatusClientError() throws Exception {
        long userId = 1L;

        CreationBookingDto creationBookingDto = CreationBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(1).toString())
                .end(LocalDateTime.now().minusHours(1).toString())
                .build();

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(creationBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        verify(bookingService, never()).create(anyLong(), any(CreationBookingDto.class));
    }

    @Test
    void create_whenCreationBookingDtoEndBeforeStart_thenResponseStatusClientError() throws Exception {
        long userId = 1L;

        CreationBookingDto creationBookingDto = CreationBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(1).toString())
                .end(LocalDateTime.now().plusMinutes(30).toString())
                .build();

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(creationBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        verify(bookingService, never()).create(anyLong(), any(CreationBookingDto.class));
    }

    @Test
    void create_whenCreationBookingDtoEndEqualsStart_thenResponseStatusClientError() throws Exception {
        long userId = 1L;

        String start = LocalDateTime.now().plusDays(1).toString();

        CreationBookingDto creationBookingDto = CreationBookingDto.builder()
                .itemId(1L)
                .start(start)
                .end(start)
                .build();

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(creationBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        verify(bookingService, never()).create(anyLong(), any(CreationBookingDto.class));
    }

    @Test
    void create_whenCreationBookingDtoStartBeforeNow_thenResponseStatusClientError() throws Exception {
        long userId = 1L;

        CreationBookingDto creationBookingDto = CreationBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().minusMinutes(1).toString())
                .end(LocalDateTime.now().plusDays(1).toString())
                .build();

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(creationBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        verify(bookingService, never()).create(anyLong(), any(CreationBookingDto.class));
    }

    @Test
    void confirmStatus_whenBookingIdValid_thenResponseStatusOkWithBookingDtoInBody() throws Exception {
        long userId = 1L;
        long bookingId = 1L;

        BookingDto expectedResult = BookingDto.builder()
                .id(1L)
                .start("2023-07-05T15:00:00.000000")
                .end("2023-07-06T20:00:00.000000")
                .item(null)
                .booker(null)
                .status("WAITING")
                .build();

        when(bookingService.confirmStatus(anyLong(), anyLong(), anyString())).thenReturn(expectedResult);

        mvc.perform(patch("/bookings/{id}?approved=true", bookingId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedResult.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(expectedResult.getStart())))
                .andExpect(jsonPath("$.end", is(expectedResult.getEnd())))
                .andExpect(jsonPath("$.item", is(nullValue(ItemDto.class))))
                .andExpect(jsonPath("$.booker", is(nullValue(UserDto.class))))
                .andExpect(jsonPath("$.status", is(expectedResult.getStatus())));
    }

    @Test
    void confirmStatus_whenBookingIdLessThenZero_thenResponseStatusServerError() throws Exception {
        long userId = 1L;
        long bookingId = -1L;

        mvc.perform(patch("/bookings/{id}?approved=true", bookingId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(bookingService, never()).confirmStatus(anyLong(), anyLong(), anyString());
    }

    @Test
    void getById_whenBookingIdValid_thenResponseStatusOkWithBookingDtoInBody() throws Exception {
        long userId = 1L;
        long bookingId = 1L;

        BookingDto expectedResult = BookingDto.builder()
                .id(1L)
                .start("2023-07-05T15:00:00.000000")
                .end("2023-07-06T20:00:00.000000")
                .item(null)
                .booker(null)
                .status("WAITING")
                .build();

        when(bookingService.getById(anyLong(), anyLong())).thenReturn(expectedResult);

        mvc.perform(get("/bookings/{id}", bookingId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedResult.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(expectedResult.getStart())))
                .andExpect(jsonPath("$.end", is(expectedResult.getEnd())))
                .andExpect(jsonPath("$.item", is(nullValue(ItemDto.class))))
                .andExpect(jsonPath("$.booker", is(nullValue(UserDto.class))))
                .andExpect(jsonPath("$.status", is(expectedResult.getStatus())));
    }

    @Test
    void getById_whenBookingIdLessThanZero_thenResponseStatusServerError() throws Exception {
        long bookingId = -1L;
        long userId = -1L;

        mvc.perform(get("/bookings/{id}", bookingId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(bookingService, never()).getById(anyLong(), anyLong());
    }

    @Test
    void getAllByUser_whenFromAndSizeValid_thenResponseStatusOkWithBookingDtoCollectionInBody() throws Exception {
        long userId = 1L;

        BookingDto expectedResult = BookingDto.builder()
                .id(1L)
                .start("2023-07-05T15:00:00.000000")
                .end("2023-07-06T20:00:00.000000")
                .item(null)
                .booker(null)
                .status("WAITING")
                .build();

        when(bookingService.getAllByUser(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(expectedResult));

        mvc.perform(get("/bookings?from=0&size=10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(expectedResult.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(expectedResult.getStart())))
                .andExpect(jsonPath("$[0].end", is(expectedResult.getEnd())))
                .andExpect(jsonPath("$[0].item", is(nullValue(ItemDto.class))))
                .andExpect(jsonPath("$[0].booker", is(nullValue(UserDto.class))))
                .andExpect(jsonPath("$[0].status", is(expectedResult.getStatus())));
    }

    @Test
    void getAllByUser_whenFromOrSizeNotValid_thenResponseStatusServerError() throws Exception {
        long userId = 1L;

        mvc.perform(get("/bookings?from=-1&size=10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        mvc.perform(get("/bookings?from=0&size=0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        mvc.perform(get("/bookings?from=1&size=0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        mvc.perform(get("/bookings?from=0&size=-1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(bookingService, never()).getAllByItemOwner(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void getAllByItemOwner_whenFromAndSizeValid_thenResponseStatusOkWithBookingDtoCollectionInBody() throws Exception {
        long userId = 1L;

        BookingDto expectedResult = BookingDto.builder()
                .id(1L)
                .start("2023-07-05T15:00:00.000000")
                .end("2023-07-06T20:00:00.000000")
                .item(null)
                .booker(null)
                .status("WAITING")
                .build();

        when(bookingService.getAllByItemOwner(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(expectedResult));

        mvc.perform(get("/bookings/owner?from=0&size=10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(expectedResult.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(expectedResult.getStart())))
                .andExpect(jsonPath("$[0].end", is(expectedResult.getEnd())))
                .andExpect(jsonPath("$[0].item", is(nullValue(ItemDto.class))))
                .andExpect(jsonPath("$[0].booker", is(nullValue(UserDto.class))))
                .andExpect(jsonPath("$[0].status", is(expectedResult.getStatus())));

        verify(bookingService, never()).getAllByUser(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void getAllByItemOwner_whenFromOrSizeNotValid_thenResponseStatusServerError() throws Exception {
        long userId = 1L;

        mvc.perform(get("/bookings/owner?from=-1&size=10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        mvc.perform(get("/bookings/owner?from=0&size=0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        mvc.perform(get("/bookings/owner?from=1&size=0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        mvc.perform(get("/bookings/owner?from=0&size=-1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(bookingService, never()).getAllByItemOwner(anyLong(), anyString(), anyInt(), anyInt());
    }
}