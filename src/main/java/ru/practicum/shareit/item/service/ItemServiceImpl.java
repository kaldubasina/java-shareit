package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.Constants.*;

@Service
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public Item addNew(Item item, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
        item.setOwner(user);
        return itemRepository.save(item);
    }

    @Override
    public Item update(Item item, long itemId, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
        Item itemForUpdate = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с id %d не найдена", item.getId())));
        if (!itemForUpdate.getOwner().equals(user)) {
            throw new NotFoundException(String.format("Вы не владеете вещью с id %d", itemForUpdate.getId()));
        }
        if (item.getName() != null && !item.getName().isBlank()) {
            itemForUpdate.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            itemForUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null && itemForUpdate.getAvailable() != item.getAvailable()) {
            itemForUpdate.setAvailable(item.getAvailable());
        }
        return itemRepository.save(itemForUpdate);
    }

    @Override
    @Transactional(readOnly = true)
    public Item getByItemId(long itemId, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с id %d не найдена", itemId)));
        item.setComments(commentRepository.findByItemId(itemId));
        if (item.getOwner().equals(user)) {
            List<Booking> lastBookings = bookingRepository.findByItem_OwnerIdAndStartBeforeAndStatusNot(userId,
                    LocalDateTime.now(),
                    Status.REJECTED,
                    SORT_BY_START_DATE_DESC);
            List<Booking> nextBookings = bookingRepository.findByItem_OwnerIdAndStartAfterAndStatusNot(userId,
                    LocalDateTime.now(),
                    Status.REJECTED,
                    SORT_BY_START_DATE_ASC);
            setLastAndNextBookings(lastBookings, nextBookings, item);
        }
        return item;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> getByUser(long userId) {
        List<Item> items = itemRepository.findByOwnerId(userId);
        List<Comment> comments = commentRepository.findByItemIdIn(items.stream()
                .map(Item::getId)
                .collect(Collectors.toList()));
        List<Booking> lastBookings = bookingRepository.findByItem_OwnerIdAndStartBeforeAndStatusNot(userId,
                LocalDateTime.now(),
                Status.REJECTED,
                SORT_BY_START_DATE_DESC);
        List<Booking> nextBookings = bookingRepository.findByItem_OwnerIdAndStartAfterAndStatusNot(userId,
                LocalDateTime.now(),
                Status.REJECTED,
                SORT_BY_START_DATE_ASC);
        items.forEach(item -> item.setComments(comments.stream()
                .filter(c -> c.getItem().getId() == item.getId())
                .collect(Collectors.toList())));
        items.forEach(item -> setLastAndNextBookings(lastBookings, nextBookings, item));
        return items;
    }

    private void setLastAndNextBookings(List<Booking> lastBookings,
                                        List<Booking> nextBookings,
                                        Item item) {
        lastBookings.stream()
                .filter(b -> b.getItem().getId() == item.getId())
                .findFirst()
                .ifPresent(item::setLastBooking);
        nextBookings.stream()
                .filter(b -> b.getItem().getId() == item.getId())
                .findFirst()
                .ifPresent(item::setNextBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> searchByText(String text) {
        if (!text.isBlank()) {
            return itemRepository.findByAvailableTrueAndDescriptionContainingOrAvailableTrueAndNameContainingAllIgnoreCase(text, text);
        }
        return Collections.emptyList();
    }

    @Override
    public Comment addComment(Comment comment, long itemId, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с id %d не найдена", itemId)));
        List<Booking> bookings = bookingRepository.findByBookerIdAndItemIdAndStatusAndEndBefore(
                userId,
                itemId,
                Status.APPROVED,
                LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new NotAvailableException(
                    String.format("Пользователь с id %d не брал в аренду вещь с id %d",
                            userId,
                            itemId));
        }
        return commentRepository.save(comment.toBuilder()
                .author(user)
                .item(item)
                .created(LocalDateTime.now())
                .build());
    }
}
