package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> itemDtoJacksonTester;

    @Test
    void itemDtoTest() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .lastBooking(null)
                .nextBooking(null)
                .comments(null)
                .requestId(1L)
                .build();

        JsonContent<ItemDto> json = itemDtoJacksonTester.write(itemDto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(json).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(json).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(json).extractingJsonPathNumberValue("$.lastBooking").isEqualTo(itemDto.getLastBooking());
        assertThat(json).extractingJsonPathNumberValue("$.nextBooking").isEqualTo(itemDto.getNextBooking());
        assertThat(json).extractingJsonPathNumberValue("$.comments").isEqualTo(itemDto.getComments());
        assertThat(json).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }
}
