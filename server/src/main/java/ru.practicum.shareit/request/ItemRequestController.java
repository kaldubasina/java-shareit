package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.Constants.REQUEST_HEADER_USER_ID;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto add(@RequestBody ItemRequestDto itemRequestDto,
                              @RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        return ItemRequestMapper.toItemRequestDto(requestService.add(itemRequest, userId));
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@PathVariable long requestId,
                                  @RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        return ItemRequestMapper.toItemRequestDto(requestService.getById(requestId, userId));
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getAll(@RequestParam(value = "from", defaultValue = "0") int from,
                                             @RequestParam(value = "size", defaultValue = "5") int size,
                                             @RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        return requestService.getAll(userId, from, size).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @GetMapping
    public Collection<ItemRequestDto> getByUserId(@RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        return requestService.getByUserId(userId).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }
}
