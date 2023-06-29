package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemInMemoryStorage;
import ru.practicum.shareit.user.dao.UserInMemoryStorage;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final ItemInMemoryStorage itemStorage;
    private final UserInMemoryStorage userStorage;

    @Autowired
    public UserServiceImpl(ItemInMemoryStorage itemStorage, UserInMemoryStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public User addNew(User user) {
        if (isEmailUsed(user.getEmail())) {
            throw new AlreadyExistException(
                    String.format("Пользователь с почтовым адресом %s уже существует", user.getEmail())
            );
        }
        return userStorage.addNew(user);
    }

    @Override
    public User update(User user, int userId) {
        User forUpdate = userStorage.getById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
        if (user.getEmail() != null && !user.getEmail().equals(forUpdate.getEmail())) {
            if (isEmailUsed(user.getEmail())) {
                throw new AlreadyExistException(String.format("Почтовый адрес %s уже используется", user.getEmail()));
            }
            forUpdate.setEmail(user.getEmail());
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            forUpdate.setName(user.getName());
        }
        return forUpdate;
    }

    @Override
    public User getById(int userId) {
        return userStorage.getById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
    }

    @Override
    public void delete(int userId) {
        userStorage.delete(userId);
        itemStorage.deleteAllByUserId(userId);
    }

    @Override
    public List<User> getAll() {
        return userStorage.getAll();
    }

    private boolean isEmailUsed(String email) {
        return userStorage.getAll().stream()
                .map(User::getEmail)
                .anyMatch(s -> s.equals(email));
    }
}
