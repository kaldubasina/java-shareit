package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemInMemoryStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserInMemoryStorage;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemInMemoryStorage itemStorage;
    private final UserInMemoryStorage userStorage;

    @Autowired
    public ItemServiceImpl(ItemInMemoryStorage itemStorage, UserInMemoryStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Item addNew(Item item, int userId) {
        User user = userStorage.getById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
        return itemStorage.addNew(item, user);
    }

    @Override
    public Item update(Item item, int itemId, int userId) {
        Item forUpdate = itemStorage.getById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с id %d не найдена", item.getId())));
        if (forUpdate.getOwner().getId() != userId) {
            throw new NotFoundException(String.format("Вы не владеете вещью с id %d", item.getId()));
        }
        if (item.getName() != null && !item.getName().isBlank()) {
            forUpdate.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            forUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null && forUpdate.getAvailable() != item.getAvailable()) {
            forUpdate.setAvailable(item.getAvailable());
        }
        return forUpdate;
    }

    @Override
    public Item getById(int itemId) {
        return itemStorage.getById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с id %d не найдена", itemId)));
    }

    @Override
    public List<Item> getByUser(int userId) {
        return itemStorage.getByUserId(userId);
    }

    @Override
    public List<Item> searchByText(String text) {
        if (!text.isBlank()) {
            return itemStorage.searchByText(text);
        }
        return Collections.emptyList();
    }
}
