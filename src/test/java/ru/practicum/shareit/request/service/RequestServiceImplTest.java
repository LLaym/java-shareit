package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.CreationRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestServiceImplTest {
    private final EntityManager em;
    private final RequestService requestService;
    private User requestor;

    @BeforeEach
    public void addData() {
        requestor = new User(null, "John", "john@example.com");
        em.persist(requestor);
        em.flush();
    }

    @Test
    void create_whenSuccess_thenCreateAndRequestDtoReturned() {
        CreationRequestDto creationRequestDto = new CreationRequestDto();
        creationRequestDto.setDescription("Something like hammer maybe?");

        RequestDto resultRequestDto = requestService.create(requestor.getId(), creationRequestDto);


        TypedQuery<Request> query = em.createQuery("Select r from Request r where r.description = :description", Request.class);
        Request request = query.setParameter("description", creationRequestDto.getDescription()).getSingleResult();

        assertThat(resultRequestDto.getId(), notNullValue());
        assertThat(resultRequestDto.getDescription(), equalTo(creationRequestDto.getDescription()));
        assertThat(resultRequestDto.getRequestorId(), equalTo(requestor.getId()));

        assertThat(request.getId(), notNullValue());
        assertThat(request.getDescription(), equalTo(creationRequestDto.getDescription()));
        assertThat(request.getRequestor().getId(), equalTo(requestor.getId()));
    }

    @Test
    void getById_whenPersist_thenRequestDtoReturned() {
        Request request = new Request();
        request.setRequestor(requestor);
        request.setDescription("Something like hummer");
        em.persist(request);
        em.flush();

        RequestDto resultRequestDto = requestService.getById(requestor.getId(), request.getId());

        assertThat(resultRequestDto, notNullValue());
        assertThat(resultRequestDto.getDescription(), equalTo(request.getDescription()));
        assertThat(resultRequestDto.getRequestorId(), equalTo(requestor.getId()));
    }

    @Test
    void getAllByUser_whenPersist_thenCollectionOfRequestDtoReturned() {
        Request request = new Request();
        request.setRequestor(requestor);
        request.setDescription("Something like hummer");
        em.persist(request);
        em.flush();

        List<RequestDto> resultRequestDtos = requestService.getAllByUser(requestor.getId());

        assertThat(resultRequestDtos.isEmpty(), is(false));
        assertThat(resultRequestDtos.get(0), notNullValue());
        assertThat(resultRequestDtos.get(0).getDescription(), equalTo(request.getDescription()));
        assertThat(resultRequestDtos.get(0).getRequestorId(), equalTo(requestor.getId()));
    }

    @Test
    void getAll_whenPersist_thenCollectionOfRequestDtoReturned() {
        User anotherUser = new User(null, "Kyle", "kyle@example.com");
        em.persist(anotherUser);
        em.flush();

        Request request = new Request();
        request.setRequestor(requestor);
        request.setDescription("Something like hummer");
        em.persist(request);
        em.flush();

        List<RequestDto> resultRequestDtos = requestService.getAll(anotherUser.getId(), 0, 10);

        assertThat(resultRequestDtos.isEmpty(), is(false));
        assertThat(resultRequestDtos.get(0), notNullValue());
        assertThat(resultRequestDtos.get(0).getDescription(), equalTo(request.getDescription()));
        assertThat(resultRequestDtos.get(0).getRequestorId(), equalTo(requestor.getId()));
    }
}