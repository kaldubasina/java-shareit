package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User addNew(User user);

    User update(User user, int userId);

    User getById(int userId);

    void delete(int userId);

    List<User> getAll();
}
