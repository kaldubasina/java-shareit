package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validators.EntityValidator.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import java.util.Collections;

import static ru.practicum.shareit.util.Constants.REQUEST_HEADER_USER_ID;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public Object add(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                      @Validated(OnCreate.class) @RequestBody ItemDto itemDto) {
        log.info("Creating item {}, userId={}", itemDto, userId);
        return itemClient.saveItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public Object updateById(@RequestBody ItemDto itemDto,
                             @PathVariable long itemId,
                             @RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        log.info("Update item with itemId={}, userId={}", itemId, userId);
        return itemClient.updateById(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public Object getById(@PathVariable long itemId,
                          @RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        log.info("Get item with itemId={}, userId={}", itemId, userId);
        return itemClient.getById(userId, itemId);
    }

    @GetMapping
    public Object getByUser(@RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
                            @RequestParam(value = "size", defaultValue = "5") @Positive int size,
                            @RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        log.info("Get items with ownerId={}", userId);
        return itemClient.getByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public Object searchByText(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                               @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
                               @RequestParam(value = "size", defaultValue = "5") @Positive int size,
                               @RequestParam("text") String text) {
        log.info("Search items with text, userId={}", userId);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemClient.searchByText(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public Object addComment(@RequestBody @Valid CommentDto commentDto,
                             @PathVariable long itemId,
                             @RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        log.info("Creating comment {} to item with itemId={}, userId={}", commentDto, itemId, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
