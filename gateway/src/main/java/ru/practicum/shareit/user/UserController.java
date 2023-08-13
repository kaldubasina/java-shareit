package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validators.EntityValidator.*;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public Object add(@Validated(OnCreate.class) @RequestBody UserDto userDto) {
        log.info("Creating user {}", userDto);
        return userClient.saveUser(userDto);
    }

    @PatchMapping("/{userId}")
    public Object update(@Validated(OnUpdate.class) @RequestBody UserDto userDto,
                          @PathVariable long userId) {
        log.info("Update user with userId={}", userId);
        return userClient.update(userId, userDto);
    }

    @GetMapping("/{userId}")
    public Object getById(@PathVariable long userId) {
        log.info("Get user with userId={}", userId);
        return userClient.getById(userId);
    }

    @DeleteMapping("/{userId}")
    public Object delete(@PathVariable long userId) {
        log.info("Deleting user with userId={}", userId);
        try {
            userClient.deleteById(userId);
            return "Deletion complete";
        } catch (Exception e) {
            return "Deletion error";
        }
    }

    @GetMapping
    public Object getAll() {
        log.info("Get all users");
        return userClient.getAll();
    }
}
