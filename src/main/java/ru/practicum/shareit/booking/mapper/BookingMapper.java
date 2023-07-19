package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

@UtilityClass
public class BookingMapper {
    public ResponseBookingDto toGetBookingDto(Booking booking) {
        return ResponseBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(UserMapper.toBookerDto(booking.getBooker()))
                .item(ItemMapper.toItemForBookingDto(booking.getItem()))
                .build();
    }

    public Booking createDtoToBooking(RequestBookingDto bookingDto) {
        return Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(Status.WAITING)
                .build();
    }

    public BookingDto toBookingForItemDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
