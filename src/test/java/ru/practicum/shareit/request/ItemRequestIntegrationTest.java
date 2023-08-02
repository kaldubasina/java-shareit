package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestIntegrationTest {
    private final ItemRequestService requestService;
    private final UserService userService;
    private final EntityManager em;

    private User user;
    private ItemRequest request;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("user")
                .email("user@user.user")
                .build();

        request = ItemRequest.builder()
                .description("description")
                .requester(user)
                .created(LocalDateTime.now())
                .build();

        userService.add(user);
    }

    @Test
    @DirtiesContext
    void shouldSaveRequest() {
        requestService.add(request, 1L);

        TypedQuery<ItemRequest> query = em.createQuery("Select ir from ItemRequest ir where ir.id = :id",
                ItemRequest.class);
        ItemRequest savedRequest = query.setParameter("id", 1L).getSingleResult();

        assertThat(savedRequest.getId(), equalTo(1L));
        assertThat(savedRequest.getDescription(), equalTo(request.getDescription()));
        assertThat(savedRequest.getRequester(), equalTo(user));
        assertThat(savedRequest.getCreated(), notNullValue());
    }

    @Test
    @DirtiesContext
    void shouldReturnRequestById() {
        requestService.add(request, 1L);

        ItemRequest getRequest = requestService.getById(1L, 1L);

        assertThat(getRequest.getId(), equalTo(1L));
        assertThat(getRequest.getDescription(), equalTo(request.getDescription()));
        assertThat(getRequest.getRequester(), equalTo(user));
        assertThat(getRequest.getCreated(), notNullValue());
    }

    @Test
    @DirtiesContext
    void shouldReturnRequestByUserId() {
        requestService.add(request, 1L);

        List<ItemRequest> requests = requestService.getByUserId(1L);

        assertThat(requests.size(), equalTo(1));
        assertThat(requests.get(0).getId(), equalTo(request.getId()));
        assertThat(requests.get(0).getDescription(), equalTo(request.getDescription()));
    }

    @Test
    @DirtiesContext
    void shouldReturnRequestByOtherUsers() {
        userService.add(User.builder()
                .name("newName")
                .email("new@new.new")
                .build());
        requestService.add(request, 2L);

        List<ItemRequest> requests = requestService.getAll(1L, 0, 5);

        assertThat(requests.size(), equalTo(1));
        assertThat(requests.get(0).getId(), equalTo(request.getId()));
        assertThat(requests.get(0).getDescription(), equalTo(request.getDescription()));
    }
}
