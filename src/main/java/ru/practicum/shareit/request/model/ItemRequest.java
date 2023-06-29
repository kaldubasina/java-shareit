package ru.practicum.shareit.request.model;

import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;

/**
 * TODO Sprint add-item-requests.
 */
public class ItemRequest {
    private int id;
    private String description;
    private User requestor;
    private Timestamp created;
}
