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
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
    public ResponseBookingDto add(@Valid @RequestBody RequestBookingDto bookingDto,
                                  @RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        Booking booking = bookingService.add(
                BookingMapper.requestBookingDtoToBooking(bookingDto),
                bookingDto.getItemId(),
                userId);
        return BookingMapper.toResponseBookingDto(booking);
    }

    @PatchMapping("/{bookingId}")
    public ResponseBookingDto bookingDecision(@PathVariable long bookingId,
                                              @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                              @RequestParam("approved") Boolean isApproved) {
        Booking booking = bookingService.bookingDecision(bookingId, userId, isApproved);
        return BookingMapper.toResponseBookingDto(booking);
    }

    @GetMapping("/{bookingId}")
    public ResponseBookingDto getBookingById(@PathVariable long bookingId,
                                             @RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        Booking booking = bookingService.getByBookingIdAndUserId(bookingId, userId);
        return BookingMapper.toResponseBookingDto(booking);
    }

    @GetMapping
    public Collection<ResponseBookingDto> getByStateAndUserId(
            @RequestParam(value = "state", defaultValue = "ALL")
            @AvailableEnumValue(enumClass = State.class) String state,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(value = "size", defaultValue = "5") @Positive int size,
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        return bookingService.getByStateAndUserId(State.valueOf(state), userId, from, size)
                .stream()
                .map(BookingMapper::toResponseBookingDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public Collection<ResponseBookingDto> getAllByStateAndUserId(
            @RequestParam(value = "state", defaultValue = "ALL")
            @AvailableEnumValue(enumClass = State.class) String state,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(value = "size", defaultValue = "5") @Positive int size,
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        return bookingService.getAllByStateAndUserId(State.valueOf(state), userId, from, size)
                .stream()
                .map(BookingMapper::toResponseBookingDto)
                .collect(Collectors.toList());
    }
}
