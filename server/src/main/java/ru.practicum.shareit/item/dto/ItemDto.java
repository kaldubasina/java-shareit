package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    @Nullable
    private Long requestId;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;
}
