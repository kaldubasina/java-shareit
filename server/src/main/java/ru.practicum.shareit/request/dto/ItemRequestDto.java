package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class ItemRequestDto {
    private long id;
    @NotBlank
    private String description;
    private UserDto requester;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSS")
    private LocalDateTime created;
    private List<ItemDto> items;
}
