package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.dto.CreationRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RequestMapper requestMapper;
    @InjectMocks
    private RequestServiceImpl requestService;

    @Test
    void create_whenUserFound_thenCreateAndRequestDtoReturned() {
        long userId = 1L;
        CreationRequestDto creationRequestDto = new CreationRequestDto("Need something like hammer");
        User user = new User();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Request request = new Request();
        request.setDescription("Need something like hammer");

        when(requestMapper.toRequest(creationRequestDto, user)).thenReturn(request);
        when(requestRepository.save(request)).thenReturn(request);

        RequestDto requestDto = new RequestDto();
        requestDto.setDescription("Need something like hammer");

        when(requestMapper.toRequestDto(request)).thenReturn(requestDto);

        RequestDto resultRequestDto = requestService.create(userId, creationRequestDto);

        assertEquals(resultRequestDto, requestDto);
        verify(userRepository, times(1)).findById(userId);
        verify(requestMapper, times(1)).toRequest(creationRequestDto, user);
        verify(requestRepository, times(1)).save(request);
        verify(requestMapper, times(1)).toRequestDto(request);
    }

    @Test
    void create_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 999L;
        CreationRequestDto creationRequestDto = new CreationRequestDto("Need something like hammer");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.create(userId, creationRequestDto));
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getById_whenUserAndRequestFound_thenRequestDtoReturned() {
        long userId = 1L;
        long requestId = 1L;
        User user = new User();
        Request request = new Request();
        RequestDto expectedRequestDto = new RequestDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findById(userId)).thenReturn(Optional.of(request));
        when(requestMapper.toRequestDto(request)).thenReturn(expectedRequestDto);

        RequestDto resultRequest = requestService.getById(userId, requestId);

        assertEquals(resultRequest, expectedRequestDto);
        verify(userRepository).findById(userId);
        verify(requestRepository).findById(requestId);
        verify(requestMapper).toRequestDto(request);
    }

    @Test
    void getById_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 999L;
        long requestId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.getById(userId, requestId));
        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getById_whenRequestNotFound_thenNotFoundExceptionThrown() {
        long userId = 1L;
        long requestId = 999L;
        User user = new User();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.getById(userId, requestId));
        verify(userRepository).findById(userId);
        verify(requestRepository).findById(requestId);
        verifyNoMoreInteractions(userRepository, requestRepository);
    }

    @Test
    void getAllByUser_whenUserFound_thenCollectionOfRequestDtoReturned() {
        long userId = 1L;
        User requestor = new User();
        Request request = new Request();
        RequestDto requestDto = new RequestDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(requestor));
        when(requestRepository.findAllByRequestor(requestor, Sort.by("created").descending()))
                .thenReturn(List.of(request));
        when(requestMapper.toRequestDto(request)).thenReturn(requestDto);

        List<RequestDto> resultRequests = requestService.getAllByUser(userId);

        assertFalse(resultRequests.isEmpty());
        assertEquals(resultRequests.size(), 1);
        assertEquals(resultRequests.get(0), requestDto);
    }

    @Test
    void getAllByUser_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.getAllByUser(userId));
        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAll_whenUserNotOwnerOfRequest_thenCollectionOfRequestDtoOfOtherUsersReturned() {
        long userId = 1L;
        User user = new User(userId, "John", "john@example.com");

        Request request = new Request();
        request.setRequestor(new User(2L, "Paul", "paul@example.com"));

        Page<Request> page = new PageImpl<>(List.of(request));
        RequestDto requestDto = new RequestDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findAll(any(PageRequest.class))).thenReturn(page);
        when(requestMapper.toRequestDto(request)).thenReturn(requestDto);

        List<RequestDto> resultRequests = requestService.getAll(userId, 0, 10);

        assertFalse(resultRequests.isEmpty());
        assertEquals(resultRequests.size(), 1);
        assertEquals(resultRequests.get(0), requestDto);
    }

    @Test
    void getAll_whenUserIsOwnerOfRequest_thenEmptyCollectionReturned() {
        long userId = 1L;
        User user = new User(userId, "John", "john@example.com");

        Request request = new Request();
        request.setRequestor(user);

        Page<Request> page = new PageImpl<>(List.of(request));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findAll(any(PageRequest.class))).thenReturn(page);

        List<RequestDto> resultRequests = requestService.getAll(userId, 0, 10);

        assertTrue(resultRequests.isEmpty());
    }

    @Test
    void getAll_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.getAll(userId, 0, 10));
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }
}