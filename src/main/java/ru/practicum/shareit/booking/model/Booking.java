package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;

/**
 * TODO Sprint add-bookings.
 */
public class Booking {
    private int id;
    private Timestamp start;
    private Timestamp end;
    private Item item;
    private User booker;
    private String status;
}
