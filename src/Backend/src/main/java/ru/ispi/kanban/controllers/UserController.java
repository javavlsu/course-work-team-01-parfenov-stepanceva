package ru.ispi.kanban.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.ispi.kanban.dto.UserDto;
import ru.ispi.kanban.payloads.UpdateNamePayload;
import ru.ispi.kanban.payloads.UpdatePasswordPayload;
import ru.ispi.kanban.services.UserService;
import ru.ispi.kanban.utils.SecurityUtils;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users/profile")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserDto> get() {
        return ResponseEntity.ok(userService.getById(SecurityUtils.requireCurrentUserId()));
    }

    @PatchMapping("/name")
    public ResponseEntity<UserDto> updateName(@Valid @RequestBody UpdateNamePayload payload) {
        return ResponseEntity.ok(userService.updateName(SecurityUtils.requireCurrentUserId(), payload));
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody UpdatePasswordPayload payload) {
        userService.updatePassword(SecurityUtils.requireCurrentUserId(), payload);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/avatar")
    public ResponseEntity<UserDto> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(userService.updateAvatar(SecurityUtils.requireCurrentUserId(), file));
    }
}
