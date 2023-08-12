package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validators.EntityValidator;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto add(@Validated(EntityValidator.OnCreate.class) @RequestBody UserDto userDto) {
        return UserMapper.toUserDto(userService.add(UserMapper.dtoToUser(userDto)));
    }

    @PatchMapping("/{userId}")
    public UserDto update(@Validated(EntityValidator.OnUpdate.class) @RequestBody UserDto userDto,
                          @PathVariable long userId) {
        return UserMapper.toUserDto(userService.update(UserMapper.dtoToUser(userDto), userId));
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable long userId) {
        return UserMapper.toUserDto(userService.getById(userId));
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        userService.delete(userId);
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        return userService.getAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
