package ru.practicum.shareit.integration.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIT {
    private final EntityManager em;
    private final UserService userService;

    @Test
    void create() {
        UserDto userDto = new UserDto();
        userDto.setName("John");
        userDto.setEmail("john@example.com");

        UserDto resultUserDto = userService.create(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(resultUserDto.getId(), notNullValue());
        assertThat(resultUserDto.getName(), equalTo("John"));
        assertThat(resultUserDto.getEmail(), equalTo("john@example.com"));

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void getById() {
        User user = new User(null, "John", "john@example.com");
        em.persist(user);
        em.flush();

        UserDto resultUserDto = userService.getById(user.getId());

        assertThat(resultUserDto, notNullValue());
        assertThat(resultUserDto.getName(), equalTo(user.getName()));
        assertThat(resultUserDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void update() {
        User user = new User(null, "John", "john@example.com");
        em.persist(user);
        em.flush();

        UserDto updateUser = new UserDto(null, "John Updated", "johnUpdated@example.com");

        UserDto resultUserDto = userService.update(user.getId(), updateUser);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User updatedUser = query.setParameter("email", updateUser.getEmail()).getSingleResult();

        assertThat(resultUserDto, notNullValue());
        assertThat(resultUserDto.getName(), equalTo(updateUser.getName()));
        assertThat(resultUserDto.getEmail(), equalTo(updateUser.getEmail()));

        assertThat(updatedUser, notNullValue());
        assertThat(updatedUser.getName(), equalTo(resultUserDto.getName()));
        assertThat(updatedUser.getEmail(), equalTo(resultUserDto.getEmail()));
    }

    @Test
    void deleteById() {
        User user = new User(null, "John", "john@example.com");
        em.persist(user);
        em.flush();

        userService.deleteById(user.getId());

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User nullUser = query.setParameter("email", user.getEmail()).getResultStream()
                .findFirst().orElse(null);

        assertThat(nullUser, nullValue());
    }

    @Test
    void getAll() {
        User user = new User(null, "John", "john@example.com");
        em.persist(user);
        em.flush();

        List<UserDto> resultUserDtos = userService.getAll();

        assertThat(resultUserDtos.isEmpty(), is(false));
        assertThat(resultUserDtos.get(0).getId(), notNullValue());
        assertThat(resultUserDtos.get(0).getName(), equalTo(user.getName()));
        assertThat(resultUserDtos.get(0).getEmail(), equalTo(user.getEmail()));
    }
}