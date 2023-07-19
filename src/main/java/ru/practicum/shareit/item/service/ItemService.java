package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item add(Item item, long userId);

    Item update(Item item, long itemId, long userId);

    Item getByItemId(long itemId, long userId);

    List<Item> getByUserId(long userId);

    List<Item> searchByText(String text);

    Comment addComment(Comment comment, long itemId, long userId);
}
