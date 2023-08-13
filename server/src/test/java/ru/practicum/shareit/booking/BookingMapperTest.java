package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class BookingMapperTest {

    @Test
    void mapRequestBookingDtoToBooking() {
        RequestBookingDto requestBookingDto = RequestBookingDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .itemId(1)
                .build();
        Booking booking = BookingMapper.requestBookingDtoToBooking(requestBookingDto);

        assertThat(booking.getStart(), equalTo(requestBookingDto.getStart()));
        assertThat(booking.getEnd(), equalTo(requestBookingDto.getEnd()));
        assertThat(booking.getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void mapBookingToResponseBookingDto() {
        Item item = Item.builder().id(1L).name("name").build();
        User user = User.builder().id(1L).build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();
        ResponseBookingDto responseBookingDto = BookingMapper.toResponseBookingDto(booking);

        assertThat(responseBookingDto.getId(), equalTo(booking.getId()));
        assertThat(responseBookingDto.getStart(), equalTo(booking.getStart()));
        assertThat(responseBookingDto.getEnd(), equalTo(booking.getEnd()));
        assertThat(responseBookingDto.getStatus(), equalTo(booking.getStatus()));
        assertThat(responseBookingDto.getBooker().getId(), equalTo(booking.getBooker().getId()));
        assertThat(responseBookingDto.getItem().getId(), equalTo(booking.getItem().getId()));
        assertThat(responseBookingDto.getItem().getName(), equalTo(booking.getItem().getName()));
    }
}
