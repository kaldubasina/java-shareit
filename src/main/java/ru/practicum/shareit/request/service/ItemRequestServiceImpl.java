package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public ItemRequestServiceImpl(UserRepository userRepository,
                                  ItemRepository itemRepository,
                                  ItemRequestRepository requestRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    public ItemRequest add(ItemRequest itemRequest, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());
        return requestRepository.save(itemRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequest getById(long requestId, long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
        ItemRequest itemRequest = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Запрос с id %d не найден", requestId)));
        itemRequest.setItemsOnRequest(itemRepository.findByItemRequestId(requestId));
        return itemRequest;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequest> getByUserId(long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
        List<ItemRequest> requests = requestRepository.findByRequesterId(userId);
        List<Item> items = itemRepository.findByItemRequestIdIn(requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList()));
        if (!items.isEmpty()) {
            requests.forEach(r -> r.setItemsOnRequest(items.stream()
                    .filter(i -> i.getItemRequestId() == r.getId())
                    .collect(Collectors.toList())));
        }
        return requests;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequest> getAll(long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
        Pageable sortByCreated = PageRequest.of(from / size, size, Sort.by("created").descending());
        List<ItemRequest> requests = requestRepository.findByRequesterIdNot(userId, sortByCreated);
        List<Item> items = itemRepository.findByItemRequestIdIn(requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList()));
        if (!items.isEmpty()) {
            requests.forEach(r -> r.setItemsOnRequest(items.stream()
                    .filter(i -> i.getItemRequestId() == r.getId())
                    .collect(Collectors.toList())));
        }
        return requests;
    }
}
