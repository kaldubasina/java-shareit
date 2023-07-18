package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.validators.EntityValidator.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class ItemDto {
    private long id;
    @NotBlank(groups = OnCreate.class)
    private String name;
    @NotBlank(groups = OnCreate.class)
    private String description;
    @NotNull(groups = OnCreate.class)
    private Boolean available;
    private BookingForItemDto lastBooking;
    private BookingForItemDto nextBooking;
    private List<CommentDto> comments;
}
