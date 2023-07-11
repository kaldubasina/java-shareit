package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class User {
    private int id;
    private String name;
    private String email;
}
