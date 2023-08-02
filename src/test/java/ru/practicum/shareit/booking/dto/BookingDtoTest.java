package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> bookingDtoJacksonTester;

    @Test
    void bookingDtoTest() throws Exception {
        BookingDto bookingDto = BookingDto.builder().id(1).bookerId(1).build();

        JsonContent<BookingDto> json = bookingDtoJacksonTester.write(bookingDto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
    }
}
