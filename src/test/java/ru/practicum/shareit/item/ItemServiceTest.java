package ru.practicum.shareit.item;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private CommentRepository commentRepository;

    private Item item;
    private User user;

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
    }

    @Test
    void shouldThrowUserNotFoundException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemService.add(item, 1L, nullable(Long.class)));
        assertThrows(NotFoundException.class, () ->
                itemService.update(item, 1L, 1L));
        assertThrows(NotFoundException.class, () ->
                itemService.getByItemId(1L, 1L));
        assertThrows(NotFoundException.class, () ->
                itemService.addComment(any(), 1L, 1L));
        verify(userRepository, times(4)).findById(anyLong());
    }

    @Test
    void shouldThrowItemNotFoundException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemService.update(item, 1L, 1L));
        assertThrows(NotFoundException.class, () ->
                itemService.getByItemId(1L, 1L));
        assertThrows(NotFoundException.class, () ->
                itemService.addComment(any(), 1L, 1L));
        verify(userRepository, times(3)).findById(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenNotOwnerTryToUpdateItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        item.setOwner(User.builder().id(2).build());

        assertThrows(NotFoundException.class, () ->
                itemService.update(item, 1L, 1L));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldThrowRequestNotFoundException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemService.add(item, 1L, 1L));
        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldThrowNotAvailableExceptionWhenAddCommentWithoutBooking() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(emptyList());

        assertThrows(NotAvailableException.class, () ->
                itemService.addComment(any(), 1L, 1L));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1))
                .findByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any());
    }

    @Test
    void shouldSaveItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.save(any()))
                .thenReturn(item);

        assertThat(item, equalTo(itemService.add(item, 1L, null)));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any());
        verify(requestRepository, never()).findById(anyLong());
    }

    @Test
    void shouldUpdateItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any()))
                .thenReturn(item);

        assertThat(item, equalTo(itemService.update(item, 1L, 1L)));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void shouldReturnItemById() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(anyLong()))
                .thenReturn(emptyList());
        when(bookingRepository.findByItem_OwnerIdAndStartBeforeAndStatusNot(anyLong(), any(), any(), any()))
                .thenReturn(emptyList());
        when(bookingRepository.findByItem_OwnerIdAndStartAfterAndStatusNot(anyLong(), any(), any(), any()))
                .thenReturn(emptyList());

        assertThat(item, equalTo(itemService.getByItemId(1L, 1L)));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).findByItemId(anyLong());
        verify(bookingRepository, times(1))
                .findByItem_OwnerIdAndStartBeforeAndStatusNot(anyLong(), any(), any(), any());
        verify(bookingRepository, times(1))
                .findByItem_OwnerIdAndStartAfterAndStatusNot(anyLong(), any(), any(), any());
    }

    @Test
    void shouldReturnItemsByUserId() {
        when(itemRepository.findByOwnerId(anyLong(), any()))
                .thenReturn(List.of(item));
        when(commentRepository.findByItemIdIn(anyList()))
                .thenReturn(emptyList());
        when(bookingRepository.findByItem_OwnerIdAndStartBeforeAndStatusNot(anyLong(), any(), any(), any()))
                .thenReturn(emptyList());
        when(bookingRepository.findByItem_OwnerIdAndStartAfterAndStatusNot(anyLong(), any(), any(), any()))
                .thenReturn(emptyList());

        assertThat(List.of(item), equalTo(itemService.getByUserId(1L, 0, 5)));
        verify(itemRepository, times(1)).findByOwnerId(anyLong(), any());
        verify(commentRepository, times(1)).findByItemIdIn(anyList());
        verify(bookingRepository, times(1))
                .findByItem_OwnerIdAndStartBeforeAndStatusNot(anyLong(), any(), any(), any());
        verify(bookingRepository, times(1))
                .findByItem_OwnerIdAndStartAfterAndStatusNot(anyLong(), any(), any(), any());
    }

    @Test
    void shouldSearchItemsByText() {
        when(itemRepository.findByAvailableTrueAndDescriptionContainingOrAvailableTrueAndNameContainingAllIgnoreCase(
                anyString(), anyString(), any()))
                .thenReturn(List.of(item));

        assertThat(List.of(item), equalTo(itemService.searchByText("text", 0, 5)));
        verify(itemRepository, times(1))
                .findByAvailableTrueAndDescriptionContainingOrAvailableTrueAndNameContainingAllIgnoreCase(
                        anyString(), anyString(), any());
    }

    @Test
    void shouldSaveComment() {
        Comment comment = Comment.builder().id(1L).text("text").build();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(List.of(Booking.builder().build()));
        when(commentRepository.save(any()))
                .thenReturn(comment);

        assertThat(comment, equalTo(itemService.addComment(comment, 1L, 1L)));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1))
                .findByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any());
        verify(commentRepository, times(1)).save(any());
    }
}
