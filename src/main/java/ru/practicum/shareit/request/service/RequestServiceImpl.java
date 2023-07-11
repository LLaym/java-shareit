package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.CreationRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

    @Transactional
    @Override
    public RequestDto create(long userId, CreationRequestDto creationRequestDto) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Request request = requestMapper.toRequest(creationRequestDto, requestor);

        Request requestCreated = requestRepository.save(request);

        log.info("Added new Request: {}", requestCreated);
        return requestMapper.toRequestDto(requestCreated);
    }

    @Override
    public RequestDto getById(long userId, long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));

        log.info("Provided Request: {}", request);
        return requestMapper.toRequestDto(request);
    }

    @Override
    public List<RequestDto> getAllByUser(long userId) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Sort sort = Sort.sort(Request.class).by(Request::getCreated).descending();

        log.info("Provided all Request list by User");
        return requestRepository.findAllByRequestor(requestor, sort).stream()
                .map(requestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDto> getAll(long userId, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Sort sort = Sort.sort(Request.class).by(Request::getCreated).descending();
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size, sort);

        log.info("Provided all Request list");
        return requestRepository.findAll(pageRequest).stream()
                .filter(request -> request.getRequestor().getId() != userId)
                .map(requestMapper::toRequestDto)
                .collect(Collectors.toList());
    }
}
