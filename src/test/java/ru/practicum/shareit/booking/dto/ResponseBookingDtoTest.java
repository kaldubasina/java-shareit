package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ResponseBookingDtoTest {
    @Autowired
    private JacksonTester<ResponseBookingDto> responseBookingDtoJacksonTester;

    @Test
    void requestBookingDtoTest() throws Exception {
        LocalDateTime start = LocalDateTime.of(2000, 1, 2, 3, 4);
        LocalDateTime end = LocalDateTime.of(2001, 2, 3, 16, 17);
        ResponseBookingDto requestBookingDto = ResponseBookingDto.builder()
                .id(1)
                .start(start)
                .end(end)
                .booker(null)
                .item(null)
                .build();

        JsonContent<ResponseBookingDto> json = responseBookingDtoJacksonTester.write(requestBookingDto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.start").isEqualTo("2000-01-02T03:04:00");
        assertThat(json).extractingJsonPathStringValue("$.end").isEqualTo("2001-02-03T16:17:00");
        assertThat(json).extractingJsonPathNumberValue("$.booker").isEqualTo(requestBookingDto.getBooker());
        assertThat(json).extractingJsonPathNumberValue("$.item").isEqualTo(requestBookingDto.getItem());
    }
}
