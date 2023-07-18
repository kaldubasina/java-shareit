package ru.practicum.shareit.booking.service;

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
@Transactional
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
    public Booking addNew(Booking booking, long itemId, long userId) {
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
    public Booking getBookingById(long bookingId, long userId) {
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
        if (isApproved) {
            if (booking.getStatus().equals(Status.APPROVED)) {
                throw new NotAvailableException("Бронирование уже подтверждено");
            }
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getUserBookings(State state, long bookerId) {
        userRepository.findById(bookerId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", bookerId)));
        LocalDateTime current = LocalDateTime.now();
        switch (state) {
            case ALL:
                return bookingRepository.findByBookerId(
                        bookerId,
                        SORT_BY_START_DATE_DESC);
            case CURRENT:
                return bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(
                        bookerId,
                        current,
                        current,
                        SORT_BY_START_DATE_DESC);
            case PAST:
                return bookingRepository.findByBookerIdAndEndBefore(
                        bookerId,
                        current,
                        SORT_BY_START_DATE_DESC);
            case FUTURE:
                return bookingRepository.findByBookerIdAndStartAfter(
                        bookerId,
                        current,
                        SORT_BY_START_DATE_DESC);
            case WAITING:
                return bookingRepository.findByBookerIdAndStatus(
                        bookerId,
                        Status.WAITING,
                        SORT_BY_START_DATE_DESC);
            case REJECTED:
                return bookingRepository.findByBookerIdAndStatus(
                        bookerId,
                        Status.REJECTED,
                        SORT_BY_START_DATE_DESC);
            default:
                return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getUserItemsBooking(State state, long ownerId) {
        userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", ownerId)));
        LocalDateTime current = LocalDateTime.now();
        switch (state) {
            case ALL:
                return bookingRepository.findByItem_OwnerId(
                        ownerId,
                        SORT_BY_START_DATE_DESC);
            case CURRENT:
                return bookingRepository.findByItem_OwnerIdAndStartBeforeAndEndAfter(
                        ownerId,
                        current,
                        current,
                        SORT_BY_START_DATE_DESC);
            case PAST:
                return bookingRepository.findByItem_OwnerIdAndStartBeforeAndStatusNot(
                        ownerId,
                        current,
                        Status.REJECTED,
                        SORT_BY_START_DATE_DESC);
            case FUTURE:
                return bookingRepository.findByItem_OwnerIdAndStartAfterAndStatusNot(
                        ownerId,
                        current,
                        Status.REJECTED,
                        SORT_BY_START_DATE_DESC);
            case WAITING:
                return bookingRepository.findByItem_OwnerIdAndStatus(
                        ownerId,
                        Status.WAITING,
                        SORT_BY_START_DATE_DESC);
            case REJECTED:
                return bookingRepository.findByItem_OwnerIdAndStatus(
                        ownerId,
                        Status.REJECTED,
                        SORT_BY_START_DATE_DESC);
            default:
                return Collections.emptyList();
        }
    }
}
