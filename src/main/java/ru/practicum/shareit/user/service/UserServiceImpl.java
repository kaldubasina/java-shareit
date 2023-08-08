package ru.practicum.shareit.user.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public User add(User user) {
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistException(
                    String.format("Пользователь с почтовым адресом %s уже существует", user.getEmail()));
        }
    }

    @Override
    @Transactional
    public User update(User user, long userId) {
        User userForUpdate = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
        if (user.getEmail() != null && !user.getEmail().equals(userForUpdate.getEmail())) {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new AlreadyExistException(String.format("Почтовый адрес %s уже используется", user.getEmail()));
            }
            userForUpdate.setEmail(user.getEmail());
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            userForUpdate.setName(user.getName());
        }
        return userRepository.save(userForUpdate);
    }

    @Override
    public User getById(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
    }

    @Override
    @Transactional
    public void delete(long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }
}
