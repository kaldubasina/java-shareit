package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.validators.AvailableEnumValue;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.Constants.REQUEST_HEADER_USER_ID;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseBookingDto addNew(@Valid @RequestBody RequestBookingDto bookingDto,
                                     @RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        Booking booking = bookingService.add(
                BookingMapper.createDtoToBooking(bookingDto),
                bookingDto.getItemId(),
                userId);
        return BookingMapper.toGetBookingDto(booking);
    }

    @PatchMapping("/{bookingId}")
    public ResponseBookingDto bookingDecision(@PathVariable long bookingId,
                                              @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                              @RequestParam("approved") boolean isApproved) {
        Booking booking = bookingService.bookingDecision(bookingId, userId, isApproved);
        return BookingMapper.toGetBookingDto(booking);
    }

    @GetMapping("/{bookingId}")
    public ResponseBookingDto getBookingById(@PathVariable long bookingId,
                                             @RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        Booking booking = bookingService.getByBookingIdAndUserId(bookingId, userId);
        return BookingMapper.toGetBookingDto(booking);
    }

    @GetMapping
    public Collection<ResponseBookingDto> getUserBookings(
            @RequestParam(value = "state", defaultValue = "ALL")
            @AvailableEnumValue(enumClass = State.class) String state,
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        return bookingService.getByStateAndUserId(State.valueOf(state), userId)
                .stream()
                .map(BookingMapper::toGetBookingDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public Collection<ResponseBookingDto> getUserItemsBooking(
            @RequestParam(value = "state", defaultValue = "ALL")
            @AvailableEnumValue(enumClass = State.class) String state,
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        return bookingService.getAllByStateAndUserId(State.valueOf(state), userId)
                .stream()
                .map(BookingMapper::toGetBookingDto)
                .collect(Collectors.toList());
    }
}
