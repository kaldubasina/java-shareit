package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ItemMapperTest {

    @Test
    void mapItemDtoToItem() {
        ItemDto itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
        Item item = ItemMapper.dtoToItem(itemDto);

        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    void mapItemToItemDto() {
        User user = User.builder().id(1L).name("name").build();
        Booking lastBooking = Booking.builder().id(1L).booker(user).build();
        Booking nextBooking = Booking.builder().id(2L).booker(user).build();
        Comment comment = Comment.builder()
                .id(1L)
                .text("text")
                .author(user)
                .created(LocalDateTime.now().minusDays(2))
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .comments(List.of(comment))
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .itemRequestId(1L)
                .build();

        ItemDto itemDto = ItemMapper.toItemDto(item);

        assertThat(itemDto.getId(), equalTo(item.getId()));
        assertThat(itemDto.getName(), equalTo(item.getName()));
        assertThat(itemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(item.getAvailable()));
        assertThat(itemDto.getLastBooking().getId(), equalTo(item.getLastBooking().getId()));
        assertThat(itemDto.getLastBooking().getBookerId(), equalTo(item.getLastBooking().getBooker().getId()));
        assertThat(itemDto.getNextBooking().getId(), equalTo(item.getNextBooking().getId()));
        assertThat(itemDto.getNextBooking().getBookerId(), equalTo(item.getNextBooking().getBooker().getId()));
        assertThat(itemDto.getComments().size(), equalTo(1));
        assertThat(itemDto.getRequestId(), equalTo(item.getItemRequestId()));
    }
}
