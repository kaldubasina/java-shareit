package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validators.EntityValidator;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.Constants.REQUEST_HEADER_USER_ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto addNew(@Validated(EntityValidator.OnCreate.class) @RequestBody ItemDto itemDto,
                          @RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        Item item = itemService.add(ItemMapper.dtoToItem(itemDto), userId);
        return ItemMapper.toItemDto(item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateById(@RequestBody ItemDto itemDto,
                              @PathVariable long itemId,
                              @RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        Item item = itemService.update(ItemMapper.dtoToItem(itemDto), itemId, userId);
        return ItemMapper.toItemDto(item);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable long itemId,
                           @RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        return ItemMapper.toItemDto(itemService.getByItemId(itemId, userId));
    }

    @GetMapping
    public Collection<ItemDto> getByUser(@RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        return itemService.getByUserId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchByText(@RequestParam("text") String text) {
        return itemService.searchByText(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestBody @Valid CommentDto commentDto,
                                 @PathVariable long itemId,
                                 @RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        Comment comment = itemService.addComment(CommentMapper.toComment(commentDto), itemId, userId);
        return CommentMapper.toCommentDto(comment);
    }
}
