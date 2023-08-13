package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookerDtoTest {
    @Autowired
    private JacksonTester<BookerDto> bookerDtoJacksonTester;

    @Test
    void userDtoTest() throws Exception {
        BookerDto bookerDto = BookerDto.builder().id(1).build();

        JsonContent<BookerDto> json = bookerDtoJacksonTester.write(bookerDto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
    }
}
