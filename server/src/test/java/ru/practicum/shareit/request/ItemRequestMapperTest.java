package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ItemRequestMapperTest {

    @Test
    void mapItemRequestDtoToItemRequest() {
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("description")
                .build();

        ItemRequest request = ItemRequestMapper.toItemRequest(requestDto);

        assertThat(request.getDescription(), equalTo(requestDto.getDescription()));
    }

    @Test
    void mapItemRequestToItemRequestDto() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("email@email.email")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .build();

        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.now())
                .requester(user)
                .itemsOnRequest(List.of(item))
                .build();

        ItemRequestDto requestDto = ItemRequestMapper.toItemRequestDto(request);

        assertThat(requestDto.getId(), equalTo(request.getId()));
        assertThat(requestDto.getDescription(), equalTo(request.getDescription()));
        assertThat(requestDto.getCreated(), equalTo(request.getCreated()));
        assertThat(requestDto.getRequester().getId(), equalTo(request.getRequester().getId()));
        assertThat(requestDto.getRequester().getName(), equalTo(request.getRequester().getName()));
        assertThat(requestDto.getRequester().getEmail(), equalTo(request.getRequester().getEmail()));
        assertThat(requestDto.getItems().size(), equalTo(request.getItemsOnRequest().size()));
    }
}
