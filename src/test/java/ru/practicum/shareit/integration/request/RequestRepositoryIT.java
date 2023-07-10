package ru.practicum.shareit.integration.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RequestRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

    private User requestor;
    private Request request;

    @BeforeEach
    public void addData() {
        requestor = new User(null, "John", "john@example.com");

        userRepository.save(requestor);

        request = new Request();
        request.setId(null);
        request.setDescription("Need something like hammer maybe");
        request.setCreated(LocalDateTime.now());
        request.setRequestor(requestor);

        requestRepository.save(request);
    }

    @Test
    void findAllByRequestor() {
        List<Request> maybeRequests = requestRepository.findAllByRequestor(
                requestor,
                Sort.unsorted()
        );

        assertFalse(maybeRequests.isEmpty());
        assertEquals(1, maybeRequests.size());

        Request actualRequest = maybeRequests.get(0);
        assertSame(request, actualRequest);
    }
}