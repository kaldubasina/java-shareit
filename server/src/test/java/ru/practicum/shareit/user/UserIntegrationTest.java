package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserIntegrationTest {
    private final UserService userService;
    private final EntityManager em;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("name")
                .email("email@email.email")
                .build();
    }

    @Test
    @DirtiesContext
    void shouldSaveUser() {
        userService.add(user);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User savedUser = query.setParameter("id", 1L).getSingleResult();

        assertThat(savedUser.getId(), equalTo(1L));
        assertThat(savedUser.getName(), equalTo(user.getName()));
        assertThat(savedUser.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    @DirtiesContext
    void shouldUpdateUser() {
        userService.add(user);
        userService.update(User.builder().name("updated").email("new@new.new").build(), 1L);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User savedUser = query.setParameter("id", 1L).getSingleResult();

        assertThat(savedUser.getId(), equalTo(1L));
        assertThat(savedUser.getName(), equalTo("updated"));
        assertThat(savedUser.getEmail(), equalTo("new@new.new"));
    }

    @Test
    @DirtiesContext
    void shouldReturnUserById() {
        userService.add(user);

        User getUser = userService.getById(1L);

        assertThat(getUser.getId(), equalTo(1L));
        assertThat(getUser.getName(), equalTo(user.getName()));
        assertThat(getUser.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    @DirtiesContext
    void shouldReturnAllUsers() {
        userService.add(user);

        List<User> users = userService.getAll();

        assertThat(users.size(), equalTo(1));
        assertThat(users.get(0).getName(), equalTo(user.getName()));
        assertThat(users.get(0).getEmail(), equalTo(user.getEmail()));
    }

    @Test
    @DirtiesContext
    void shouldDeleteUserById() {
        userService.add(user);

        assertThat(userService.getAll().size(), equalTo(1));

        userService.delete(1L);

        assertThat(userService.getAll().isEmpty(), equalTo(true));
    }
}
