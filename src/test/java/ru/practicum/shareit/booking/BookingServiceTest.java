package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    private static LocalDateTime start;
    private static LocalDateTime end;
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    private Item item;
    private User user;
    private Booking booking;

    @BeforeAll
    static void init() {
        start = LocalDateTime.now().plusDays(1);
        end = LocalDateTime.now().plusDays(2);
    }

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("name")
                .email("email@email.email")
                .build();

        item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(user)
                .build();

        booking = Booking.builder()
                .id(1L)
                .booker(user)
                .item(item)
                .status(Status.WAITING)
                .start(start)
                .end(end)
                .build();
    }

    @Test
    void shouldThrowBookingNotFoundException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.getByBookingIdAndUserId(1L, 1L));
        assertThrows(NotFoundException.class, () ->
                bookingService.bookingDecision(1L, 1L, anyBoolean()));
        verify(bookingRepository, times(2)).findById(anyLong());
    }

    @Test
    void shouldThrowUserNotFoundException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.add(booking, 1L, 1L));
        assertThrows(NotFoundException.class, () ->
                bookingService.getByBookingIdAndUserId(1L, 1L));
        assertThrows(NotFoundException.class, () ->
                bookingService.bookingDecision(1L, 1L, anyBoolean()));
        assertThrows(NotFoundException.class, () ->
                bookingService.getByStateAndUserId(any(), 1L, 1, 1));
        assertThrows(NotFoundException.class, () ->
                bookingService.getAllByStateAndUserId(any(), 1L, 1, 1));
        verify(userRepository, times(5)).findById(anyLong());
    }

    @Test
    void shouldThrowItemNotFoundException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.add(booking, 1L, 1L));
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenOwnerTryToBooking() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        assertThrows(NotFoundException.class, () ->
                bookingService.add(booking, 1L, 1L));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldThrowItemNotAvailableException() {
        item.setAvailable(false);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        assertThrows(NotAvailableException.class, () ->
                bookingService.add(booking, 1L, 2L));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldThrowTimeNotAvailableException() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        booking.setStart(start.plusDays(2));

        assertThrows(NotAvailableException.class, () ->
                bookingService.add(booking, 1L, 2L));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldSaveBooking() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.save(any()))
                .thenReturn(booking);

        assertThat(booking, equalTo(bookingService.add(booking, 1L, 2L)));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUserNotBookerOrOwner() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(User.builder().id(2).build()));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () ->
                bookingService.getByBookingIdAndUserId(1L, 1L));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenNotOwnerTryToChangeStatus() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(User.builder().id(2).build()));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () ->
                bookingService.bookingDecision(1L, 1L, true));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenStatusAlreadyApproved() {
        booking.setStatus(Status.APPROVED);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        assertThrows(NotAvailableException.class, () ->
                bookingService.bookingDecision(1L, 1L, true));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldUpdateBookingStatus() {
        booking.setStatus(Status.REJECTED);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any()))
                .thenReturn(booking);

        assertThat(booking,
                equalTo(bookingService.bookingDecision(1L, 1L, false)));

        booking.setStatus(Status.WAITING);

        assertThat(booking,
                equalTo(bookingService.bookingDecision(1L, 1L, true)));
        verify(userRepository, times(2)).findById(anyLong());
        verify(bookingRepository, times(2)).findById(anyLong());
        verify(bookingRepository, times(2)).save(any());
    }

    @Test
    void shouldReturnBookingsByBookerId() {
        List<Booking> bookings = List.of(booking);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerId(anyLong(), any()))
                .thenReturn(bookings);
        when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(anyLong(), any(), any(), any()))
                .thenReturn(bookings);
        when(bookingRepository.findByBookerIdAndEndBefore(anyLong(), any(), any()))
                .thenReturn(bookings);
        when(bookingRepository.findByBookerIdAndStartAfter(anyLong(), any(), any()))
                .thenReturn(bookings);
        when(bookingRepository.findByBookerIdAndStatus(anyLong(), any(), any()))
                .thenReturn(bookings);

        assertThat(bookings, equalTo(bookingService.getByStateAndUserId(State.ALL, 1L, 0, 5)));
        assertThat(bookings, equalTo(bookingService.getByStateAndUserId(State.FUTURE, 1L, 0, 5)));
        assertThat(bookings, equalTo(bookingService.getByStateAndUserId(State.WAITING, 1L, 0, 5)));

        booking.setStart(LocalDateTime.now().minusHours(1));
        assertThat(bookings, equalTo(bookingService.getByStateAndUserId(State.CURRENT, 1L, 0, 5)));

        booking.setEnd(LocalDateTime.now().minusMinutes(10));
        assertThat(bookings, equalTo(bookingService.getByStateAndUserId(State.PAST, 1L, 0, 5)));

        booking.setStatus(Status.REJECTED);
        assertThat(bookings, equalTo(bookingService.getByStateAndUserId(State.REJECTED, 1L, 0, 5)));
    }

    @Test
    void shouldReturnAllBookingsByItemOwnerId() {
        List<Booking> bookings = List.of(booking);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findByItem_OwnerId(anyLong(), any()))
                .thenReturn(bookings);
        when(bookingRepository.findByItem_OwnerIdAndStartBeforeAndEndAfter(anyLong(), any(), any(), any()))
                .thenReturn(bookings);
        when(bookingRepository.findByItem_OwnerIdAndStartBeforeAndStatusNot(anyLong(), any(), any(), any()))
                .thenReturn(bookings);
        when(bookingRepository.findByItem_OwnerIdAndStartAfterAndStatusNot(anyLong(), any(), any(), any()))
                .thenReturn(bookings);
        when(bookingRepository.findByItem_OwnerIdAndStatus(anyLong(), any(), any()))
                .thenReturn(bookings);

        assertThat(bookings,
                equalTo(bookingService.getAllByStateAndUserId(State.ALL, 1L, 0, 5)));
        assertThat(bookings,
                equalTo(bookingService.getAllByStateAndUserId(State.FUTURE, 1L, 0, 5)));
        assertThat(bookings,
                equalTo(bookingService.getAllByStateAndUserId(State.WAITING, 1L, 0, 5)));

        booking.setStart(LocalDateTime.now().minusHours(1));
        assertThat(bookings,
                equalTo(bookingService.getAllByStateAndUserId(State.CURRENT, 1L, 0, 5)));

        booking.setEnd(LocalDateTime.now().minusMinutes(10));
        assertThat(bookings,
                equalTo(bookingService.getAllByStateAndUserId(State.PAST, 1L, 0, 5)));

        booking.setStatus(Status.REJECTED);
        assertThat(bookings,
                equalTo(bookingService.getAllByStateAndUserId(State.REJECTED, 1L, 0, 5)));
    }
}
