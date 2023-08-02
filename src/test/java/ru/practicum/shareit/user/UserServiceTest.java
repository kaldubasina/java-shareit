package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;

    private User user;

    private User userForUpdate;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("name")
                .email("email@email.email")
                .build();

        userForUpdate = User.builder()
                .email("new@new.new")
                .build();
    }

    @Test
    void shouldThrowUserNotFoundException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                userService.getById(1L));
        assertThrows(NotFoundException.class, () ->
                userService.update(user, 1L));
        verify(userRepository, times(2)).findById(anyLong());
    }

    @Test
    void shouldThrowEmailAlreadyExistExceptionWhenUpdate() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(anyString()))
                .thenReturn(true);

        assertThrows(AlreadyExistException.class, () ->
                userService.update(userForUpdate, 1L));
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldSaveUser() {
        when(userRepository.save(any()))
                .thenReturn(user);

        assertThat(user, equalTo(userService.add(user)));
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void shouldUpdateUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(anyString()))
                .thenReturn(false);
        when(userRepository.save(any()))
                .thenReturn(user);

        assertThat(user, equalTo(userService.update(userForUpdate, 1L)));
        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void shouldReturnUserById() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        assertThat(user, equalTo(userService.getById(1L)));
        verify(userRepository, times(1)).findById(anyLong());
    }
}
