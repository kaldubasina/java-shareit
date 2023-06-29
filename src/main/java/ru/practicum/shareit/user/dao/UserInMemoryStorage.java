package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class UserInMemoryStorage {
    private static final AtomicInteger id = new AtomicInteger(1);
    private final Map<Integer, User> userMap = new HashMap<>();

    public User addNew(User user) {
        user.setId(id.getAndIncrement());
        userMap.put(user.getId(), user);
        return user;
    }

    public Optional<User> getById(int userId) {
        return Optional.ofNullable(userMap.get(userId));
    }

    public void delete(int userId) {
        userMap.remove(userId);
    }

    public List<User> getAll() {
        return new ArrayList<>(userMap.values());
    }
}
