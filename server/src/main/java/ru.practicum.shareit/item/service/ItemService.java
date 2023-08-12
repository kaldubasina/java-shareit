package ru.practicum.shareit.item.service;

import org.springframework.lang.Nullable;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item add(Item item, long userId, @Nullable Long requestId);

    Item update(Item item, long itemId, long userId);

    Item getByItemId(long itemId, long userId);

    List<Item> getByUserId(long userId, int from, int size);

    List<Item> searchByText(String text, int from, int size);

    Comment addComment(Comment comment, long itemId, long userId);
}
