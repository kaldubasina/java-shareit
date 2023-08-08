package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest add(ItemRequest itemRequest, long userId);

    ItemRequest getById(long requestId, long userId);

    List<ItemRequest> getByUserId(long userId);

    List<ItemRequest> getAll(long userId, int from, int size);
}
