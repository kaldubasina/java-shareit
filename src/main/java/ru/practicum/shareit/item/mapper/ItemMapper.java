package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForBookingDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Comparator;
import java.util.stream.Collectors;

@UtilityClass
public class ItemMapper {
    public ItemDto toItemDto(Item item) {
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        if (item.getComments() != null) {
            itemDto.setComments(item.getComments().stream()
                    .map(CommentMapper::toCommentDto)
                    .sorted(Comparator.comparing(CommentDto::getCreated))
                    .collect(Collectors.toList()));
        }
        if (item.getLastBooking() != null) {
            itemDto.setLastBooking(BookingMapper.toBookingForItemDto(item.getLastBooking()));
        }
        if (item.getNextBooking() != null) {
            itemDto.setNextBooking(BookingMapper.toBookingForItemDto(item.getNextBooking()));
        }
        return itemDto;
    }

    public Item dtoToItem(ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public ItemForBookingDto toItemForBookingDto(Item item) {
        return ItemForBookingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }
}
