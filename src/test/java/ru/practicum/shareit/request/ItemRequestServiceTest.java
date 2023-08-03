package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @InjectMocks
    private ItemRequestServiceImpl requestService;
    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    private User user;
    private ItemRequest request;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("name")
                .email("email@email.email")
                .build();

        request = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requester(user)
                .created(LocalDateTime.now())
                .itemsOnRequest(emptyList())
                .build();
    }

    @Test
    void shouldThrowUserNotFoundException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                requestService.add(request, 1L));
        assertThrows(NotFoundException.class, () ->
                requestService.getById(1L, 1L));
        assertThrows(NotFoundException.class, () ->
                requestService.getByUserId(1L));
        assertThrows(NotFoundException.class, () ->
                requestService.getAll(1L, 0, 5));
        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(3)).existsById(anyLong());
    }

    @Test
    void shouldThrowRequestNotFoundException() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                requestService.getById(1L, 1L));
        verify(userRepository, times(1)).existsById(anyLong());
        verify(requestRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldSaveRequest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestRepository.save(any()))
                .thenReturn(request);

        assertThat(request, equalTo(requestService.add(request, 1L)));
        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).save(any());
    }

    @Test
    void shouldReturnRequestById() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(itemRepository.findByItemRequestId(anyLong()))
                .thenReturn(emptyList());

        assertThat(request, equalTo(requestService.getById(1L, 1L)));
        verify(userRepository, times(1)).existsById(anyLong());
        verify(requestRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findByItemRequestId(anyLong());
    }

    @Test
    void shouldReturnRequestsByUserId() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(requestRepository.findByRequesterId(anyLong()))
                .thenReturn(List.of(request));
        when(itemRepository.findByItemRequestIdIn(anyList()))
                .thenReturn(emptyList());

        assertThat(List.of(request), equalTo(requestService.getByUserId(1L)));
        verify(userRepository, times(1)).existsById(anyLong());
        verify(requestRepository, times(1)).findByRequesterId(anyLong());
        verify(itemRepository, times(1)).findByItemRequestIdIn(anyList());
    }

    @Test
    void shouldReturnRequestsOtherUsers() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(requestRepository.findByRequesterIdNot(anyLong(), any()))
                .thenReturn(List.of(request));
        when(itemRepository.findByItemRequestIdIn(anyList()))
                .thenReturn(emptyList());

        assertThat(List.of(request), equalTo(requestService.getAll(1L, 0, 5)));
        verify(userRepository, times(1)).existsById(anyLong());
        verify(requestRepository, times(1)).findByRequesterIdNot(anyLong(), any());
        verify(itemRepository, times(1)).findByItemRequestIdIn(anyList());
    }
}
