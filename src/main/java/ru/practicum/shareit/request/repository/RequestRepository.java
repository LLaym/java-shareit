package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequestor(User requestor, Sort sort);
}
