package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto addNew(@Valid @RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") int userId) {
        Item item = itemService.addNew(ItemMapper.dtoToItem(itemDto), userId);
        return ItemMapper.toItemDto(item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateById(@RequestBody UpdateItemDto updateItemDto,
                              @PathVariable int itemId,
                              @RequestHeader("X-Sharer-User-Id") int userId) {
        Item item = itemService.update(ItemMapper.updateDtoToItem(updateItemDto), itemId, userId);
        return ItemMapper.toItemDto(item);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable int itemId) {
        return ItemMapper.toItemDto(itemService.getById(itemId));
    }

    @GetMapping
    public Collection<ItemDto> getByUser(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.getByUser(userId)
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
}
