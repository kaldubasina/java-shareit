package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
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
import static ru.practicum.shareit.booking.model.Status.APPROVED;
import static ru.practicum.shareit.booking.model.Status.WAITING;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingIntegrationTest {
    private static LocalDateTime start;
    private static LocalDateTime end;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final EntityManager em;
    private Item item;
    private User booker;
    private Booking booking;

    @BeforeAll
    static void init() {
        start = LocalDateTime.now().plusDays(1);
        end = LocalDateTime.now().plusDays(2);
    }

    @BeforeEach
    void setUp() {
        User owner = User.builder()
                .name("owner")
                .email("owner@owner.owner")
                .build();

        booker = User.builder()
                .name("booker")
                .email("booker@booker.booker")
                .build();

        item = Item.builder()
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        booking = Booking.builder()
                .status(WAITING)
                .start(start)
                .end(end)
                .build();

        userService.add(owner);
        userService.add(booker);
        itemService.add(item, 1L, null);
    }

    @Test
    @DirtiesContext
    void shouldSaveBooking() {
        bookingService.add(booking, 1L, 2L);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking savedBooking = query.setParameter("id", 1L).getSingleResult();

        assertThat(savedBooking.getId(), equalTo(1L));
        assertThat(savedBooking.getStart(), equalTo(start));
        assertThat(savedBooking.getEnd(), equalTo(end));
        assertThat(savedBooking.getStatus(), equalTo(WAITING));
        assertThat(savedBooking.getBooker(), equalTo(booker));
        assertThat(savedBooking.getItem(), equalTo(item));
    }

    @Test
    @DirtiesContext
    void shouldReturnBookingById() {
        bookingService.add(booking, 1L, 2L);

        Booking getBooking = bookingService.getByBookingIdAndUserId(1L, 1L);

        assertThat(getBooking.getId(), equalTo(1L));
        assertThat(getBooking.getStart(), equalTo(start));
        assertThat(getBooking.getEnd(), equalTo(end));
        assertThat(getBooking.getStatus(), equalTo(WAITING));
        assertThat(getBooking.getBooker(), equalTo(booker));
        assertThat(getBooking.getItem(), equalTo(item));
    }

    @Test
    @DirtiesContext
    void shouldUpdateBookingStatus() {
        bookingService.add(booking, 1L, 2L);

        bookingService.bookingDecision(1L, 1L, true);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking savedBooking = query.setParameter("id", 1L).getSingleResult();

        assertThat(savedBooking.getId(), equalTo(1L));
        assertThat(savedBooking.getStart(), equalTo(start));
        assertThat(savedBooking.getEnd(), equalTo(end));
        assertThat(savedBooking.getStatus(), equalTo(APPROVED));
        assertThat(savedBooking.getBooker(), equalTo(booker));
        assertThat(savedBooking.getItem(), equalTo(item));
    }

    @Test
    @DirtiesContext
    void shouldReturnBookingByBookerId() {
        bookingService.add(booking, 1L, 2L);

        List<Booking> bookings = bookingService.getByStateAndUserId(State.ALL, 2L, 0, 5);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(1L));
    }

    @Test
    @DirtiesContext
    void shouldReturnBookingByOwnerId() {
        bookingService.add(booking, 1L, 2L);

        List<Booking> bookings = bookingService.getAllByStateAndUserId(State.ALL, 1L, 0, 5);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(1L));
    }
}
