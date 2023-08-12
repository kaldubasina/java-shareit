package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> userDtoJacksonTester;

    @Test
    void userDtoTest() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1)
                .name("name")
                .email("email")
                .build();

        JsonContent<UserDto> json = userDtoJacksonTester.write(userDto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(json).extractingJsonPathStringValue("$.email").isEqualTo("email");
    }
}
