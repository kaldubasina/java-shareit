package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> commentDtoJacksonTester;

    @Test
    void commentDtoTest() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(1)
                .text("text")
                .authorName("name")
                .created(LocalDateTime.of(2001, 2, 3, 16, 17))
                .build();

        JsonContent<CommentDto> json = commentDtoJacksonTester.write(commentDto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.text").isEqualTo("text");
        assertThat(json).extractingJsonPathStringValue("$.authorName").isEqualTo("name");
        assertThat(json).extractingJsonPathStringValue("$.created").isEqualTo("2001-02-03T16:17:00");
    }
}
