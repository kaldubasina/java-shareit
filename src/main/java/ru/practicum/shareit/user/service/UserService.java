package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User addNew(User user);

    User update(User user, long userId);

    User getById(long userId);

    void delete(long userId);

    List<User> getAll();
}
