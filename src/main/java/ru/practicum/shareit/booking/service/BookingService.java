package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    Booking addNew(Booking booking, long itemId, long userId);

    Booking bookingDecision(long bookingId, long userId, boolean isApproved);

    List<Booking> getUserBookings(State state, long userId);

    List<Booking> getUserItemsBooking(State state, long userId);

    Booking getBookingById(long bookingId, long userId);
}
