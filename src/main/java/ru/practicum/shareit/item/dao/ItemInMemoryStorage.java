package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class ItemInMemoryStorage {
    private static final AtomicInteger id = new AtomicInteger(1);
    private final Map<Integer, Map<Integer, Item>> itemsByUserId = new HashMap<>();

    public Item addNew(Item item, User user) {
        Item newItem = item.toBuilder().id(id.getAndIncrement()).owner(user).build();
        Map<Integer, Item> itemMap = itemsByUserId.computeIfAbsent(user.getId(), v -> new HashMap<>());
        itemMap.put(newItem.getId(), newItem);
        return newItem;
    }

    public Optional<Item> getById(int itemId) {
        return itemsByUserId.values().stream()
                .map(Map::values).flatMap(Collection::stream)
                .filter(item -> item.getId() == itemId)
                .findAny();
    }

    public List<Item> getByUserId(int userId) {
        return new ArrayList<>(itemsByUserId.getOrDefault(userId, new HashMap<>()).values());
    }

    public List<Item> searchByText(String text) {
        return itemsByUserId.values().stream()
                .map(Map::values).flatMap(Collection::stream)
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }

    public void deleteAllByUserId(int userId) {
        itemsByUserId.remove(userId);
    }
}
