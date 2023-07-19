package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    Booking add(Booking booking, long itemId, long userId);

    Booking bookingDecision(long bookingId, long userId, boolean isApproved);

    List<Booking> getByStateAndUserId(State state, long userId);

    List<Booking> getAllByStateAndUserId(State state, long userId);

    Booking getByBookingIdAndUserId(long bookingId, long userId);
}
