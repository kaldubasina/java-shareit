package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemForBookingDtoTest {
    @Autowired
    private JacksonTester<ItemForBookingDto> itemForBookingDtoJacksonTester;

    @Test
    void itemDtoTest() throws Exception {
        ItemForBookingDto itemForBookingDto = ItemForBookingDto.builder()
                .id(1)
                .name("name")
                .build();

        JsonContent<ItemForBookingDto> json = itemForBookingDtoJacksonTester.write(itemForBookingDto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("name");
    }
}
