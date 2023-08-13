package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.validators.AvailableEnumValue;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Constants.REQUEST_HEADER_USER_ID;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public Object getByStateAndUserId(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
            @RequestParam(name = "state", defaultValue = "ALL")
            @AvailableEnumValue(enumClass = State.class) String state,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
            @Positive @RequestParam(name = "size", defaultValue = "5") int size) {
        log.info("Get booking with state {}, userId={}", state, userId);
        return bookingClient.getByStateAndUserId(userId, state, from, size);
    }

    @PostMapping
    public Object saveBooking(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                              @RequestBody @Valid RequestBookingDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.saveBooking(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public Object getBookingById(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                 @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public Object bookingDecision(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                  @PathVariable long bookingId,
                                  @RequestParam("approved") Boolean isApproved) {
        log.info("Update status booking {}, userId={}, status={}", bookingId, userId, isApproved);
        return bookingClient.updateBookingStatus(userId, bookingId, isApproved);
    }

    @GetMapping("/owner")
    public Object getAllByStateAndUserId(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
            @RequestParam(name = "state", defaultValue = "ALL")
            @AvailableEnumValue(enumClass = State.class) String state,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
            @Positive @RequestParam(name = "size", defaultValue = "5") int size) {
        log.info("Get all booking with state {}, userId={}", state, userId);
        return bookingClient.getAllByStateAndUserId(userId, state, from, size);
    }
}

