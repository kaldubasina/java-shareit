package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addNew(Item item, int userId);

    Item update(Item item, int itemId, int userId);

    Item getById(int itemId);

    List<Item> getByUser(int userId);

    List<Item> searchByText(String text);
}
