package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> itemRequestDtoJacksonTester;

    @Test
    void itemRequestDtoTest() throws Exception {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1)
                .description("description")
                .items(null)
                .requester(null)
                .created(LocalDateTime.of(2001, 2, 3, 16, 17))
                .build();

        JsonContent<ItemRequestDto> json = itemRequestDtoJacksonTester.write(itemRequestDto);

        assertThat(json).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.description")
                .isEqualTo("description");
        assertThat(json).extractingJsonPathNumberValue("$.items")
                .isEqualTo(itemRequestDto.getItems());
        assertThat(json).extractingJsonPathNumberValue("$.requester")
                .isEqualTo(itemRequestDto.getRequester());
        assertThat(json).extractingJsonPathStringValue("$.created")
                .isEqualTo("2001-02-03T16:17:00.0000");
    }
}
