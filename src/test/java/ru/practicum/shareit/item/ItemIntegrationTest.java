package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
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
public class ItemIntegrationTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final EntityManager em;

    private Item item;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("owner")
                .email("owner@owner.owner")
                .build();

        item = Item.builder()
                .name("name")
                .description("description")
                .available(true)
                .owner(user)
                .build();

        userService.add(user);
    }

    @Test
    @DirtiesContext
    void shouldSaveItem() {
        itemService.add(item, 1L, null);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item savedItem = query.setParameter("id", 1L).getSingleResult();

        assertThat(savedItem.getId(), equalTo(1L));
        assertThat(savedItem.getName(), equalTo(item.getName()));
        assertThat(savedItem.getDescription(), equalTo(item.getDescription()));
        assertThat(savedItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(savedItem.getOwner(), equalTo(user));
    }

    @Test
    @DirtiesContext
    void shouldUpdateItem() {
        itemService.add(item, 1L, null);
        itemService.update(Item.builder().available(false).build(), 1L, 1L);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item savedItem = query.setParameter("id", 1L).getSingleResult();

        assertThat(savedItem.getId(), equalTo(1L));
        assertThat(savedItem.getName(), equalTo(item.getName()));
        assertThat(savedItem.getDescription(), equalTo(item.getDescription()));
        assertThat(savedItem.getAvailable(), equalTo(false));
        assertThat(savedItem.getOwner(), equalTo(user));
    }

    @Test
    @DirtiesContext
    void shouldReturnItemById() {
        itemService.add(item, 1L, null);

        Item getItem = itemService.getByItemId(1L, 1L);

        assertThat(getItem.getId(), equalTo(1L));
        assertThat(getItem.getName(), equalTo(item.getName()));
        assertThat(getItem.getDescription(), equalTo(item.getDescription()));
        assertThat(getItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(getItem.getOwner(), equalTo(user));
    }

    @Test
    @DirtiesContext
    void shouldReturnItemsByUserId() {
        itemService.add(item, 1L, null);

        List<Item> items = itemService.getByUserId(1L, 0, 5);

        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getId(), equalTo(item.getId()));
    }

    @Test
    @DirtiesContext
    void shouldReturnItemsByText() {
        itemService.add(item, 1L, null);

        List<Item> items = itemService.searchByText("name", 0, 5);

        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getId(), equalTo(item.getId()));
        assertThat(items.get(0).getName(), equalTo(item.getName()));
    }

    @Test
    @DirtiesContext
    void shouldSaveComment() {
        itemService.add(item, 1L, null);
        userService.add(User.builder().name("booker").email("booker@booker.booker").build());
        bookingService.add(Booking.builder()
                        .start(LocalDateTime.now().minusDays(2))
                        .end(LocalDateTime.now().minusDays(1))
                        .status(Status.APPROVED)
                        .build(),
                1L,
                2L);

        itemService.addComment(Comment.builder().text("text").build(), 1L, 2L);

        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where c.id = :id", Comment.class);
        Comment comment = query.setParameter("id", 1L).getSingleResult();

        assertThat(comment.getId(), equalTo(1L));
        assertThat(comment.getText(), equalTo("text"));
        assertThat(comment.getItem(), equalTo(item));
        assertThat(comment.getAuthor().getName(), equalTo("booker"));
        assertThat(comment.getCreated(), notNullValue());
    }
}
