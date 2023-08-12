package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Constants.REQUEST_HEADER_USER_ID;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient requestClient;

    @PostMapping
    public Object add(@RequestBody @Valid ItemRequestDto itemRequestDto,
                      @RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        log.info("Creating itemRequest {}, userId={}", itemRequestDto, userId);
        return requestClient.saveRequest(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public Object getById(@PathVariable long requestId,
                          @RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        log.info("Get itemRequest with requestId={}, userId={}", requestId, userId);
        return requestClient.getById(userId, requestId);
    }

    @GetMapping("/all")
    public Object getAll(@RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
                         @RequestParam(value = "size", defaultValue = "5") @Positive int size,
                         @RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        log.info("Get all itemRequests");
        return requestClient.getAll(userId, from, size);
    }

    @GetMapping
    public Object getByUserId(@RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        log.info("Get itemRequests with userId={}", userId);
        return requestClient.getByUserId(userId);
    }
}
