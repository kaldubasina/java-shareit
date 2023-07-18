package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.GetBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

@UtilityClass
public class BookingMapper {
    public GetBookingDto toGetBookingDto(Booking booking) {
        return GetBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(UserMapper.toBookerDto(booking.getBooker()))
                .item(ItemMapper.toItemForBookingDto(booking.getItem()))
                .build();
    }

    public Booking createDtoToBooking(CreateBookingDto bookingDto) {
        return Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(Status.WAITING)
                .build();
    }

    public BookingForItemDto toBookingForItemDto(Booking booking) {
        return BookingForItemDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
