package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static ru.practicum.shareit.util.Constants.SORT_BY_START_DATE_DESC;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              ItemRepository itemRepository,
                              UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Booking add(Booking booking, long itemId, long userId) {
        User booker = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с id %d не найдена", itemId)));
        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("Нельзя забронировать свою вещь");
        }
        if (!item.getAvailable()) {
            throw new NotAvailableException("Вещь не доступна для бронирования");
        }
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getEnd().equals(booking.getStart())) {
            throw new NotAvailableException("Дата окончания брони должна быть позже даты начала");
        }
        Booking newBooking = booking.toBuilder()
                .booker(booker)
                .item(item)
                .build();
        return bookingRepository.save(newBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getByBookingIdAndUserId(long bookingId, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException(String.format("Бронь с id %d не найдена", bookingId)));
        if (!booking.getBooker().equals(user) && !booking.getItem().getOwner().equals(user)) {
            throw new NotFoundException("Вы не являетесь владельцем или арендатором вещи");
        }
        return booking;
    }

    @Override
    public Booking bookingDecision(long bookingId, long userId, boolean isApproved) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException(String.format("Бронь с id %d не найдена", bookingId)));
        if (!booking.getItem().getOwner().equals(user)) {
            throw new NotFoundException("Вы не являетесь владельцем вещи");
        }
        if (isApproved && booking.getStatus().equals(Status.APPROVED)) {
            throw new NotAvailableException("Бронирование уже подтверждено");
        }
        booking.setStatus(isApproved ? Status.APPROVED : Status.REJECTED);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getByStateAndUserId(State state, long bookerId, int from, int size) {
        userRepository.findById(bookerId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", bookerId)));
        LocalDateTime current = LocalDateTime.now();
        Pageable page = PageRequest.of(from / size, size, SORT_BY_START_DATE_DESC);
        switch (state) {
            case ALL:
                return bookingRepository.findByBookerId(
                        bookerId,
                        page);
            case CURRENT:
                return bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(
                        bookerId,
                        current,
                        current,
                        page);
            case PAST:
                return bookingRepository.findByBookerIdAndEndBefore(
                        bookerId,
                        current,
                        page);
            case FUTURE:
                return bookingRepository.findByBookerIdAndStartAfter(
                        bookerId,
                        current,
                        page);
            case WAITING:
                return bookingRepository.findByBookerIdAndStatus(
                        bookerId,
                        Status.WAITING,
                        page);
            case REJECTED:
                return bookingRepository.findByBookerIdAndStatus(
                        bookerId,
                        Status.REJECTED,
                        page);
            default:
                return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getAllByStateAndUserId(State state, long ownerId, int from, int size) {
        userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", ownerId)));
        LocalDateTime current = LocalDateTime.now();
        Pageable page = PageRequest.of(from / size, size, SORT_BY_START_DATE_DESC);
        switch (state) {
            case ALL:
                return bookingRepository.findByItem_OwnerId(
                        ownerId,
                        page);
            case CURRENT:
                return bookingRepository.findByItem_OwnerIdAndStartBeforeAndEndAfter(
                        ownerId,
                        current,
                        current,
                        page);
            case PAST:
                return bookingRepository.findByItem_OwnerIdAndStartBeforeAndStatusNot(
                        ownerId,
                        current,
                        Status.REJECTED,
                        page);
            case FUTURE:
                return bookingRepository.findByItem_OwnerIdAndStartAfterAndStatusNot(
                        ownerId,
                        current,
                        Status.REJECTED,
                        page);
            case WAITING:
                return bookingRepository.findByItem_OwnerIdAndStatus(
                        ownerId,
                        Status.WAITING,
                        page);
            case REJECTED:
                return bookingRepository.findByItem_OwnerIdAndStatus(
                        ownerId,
                        Status.REJECTED,
                        page);
            default:
                return Collections.emptyList();
        }
    }
}
