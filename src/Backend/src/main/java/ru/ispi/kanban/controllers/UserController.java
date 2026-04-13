package ru.ispi.kanban.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.ispi.kanban.dto.UserDto;
import ru.ispi.kanban.payloads.UpdateNamePayload;
import ru.ispi.kanban.payloads.UpdatePasswordPayload;
import ru.ispi.kanban.security.CustomUserDetails;
import ru.ispi.kanban.services.UserService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users/profile")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserDto> get(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ResponseEntity.ok(userService.getById(user.getId()));
    }

    @PatchMapping("/name")
    public ResponseEntity<UserDto> updateName(
            @Valid @RequestBody UpdateNamePayload payload,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ResponseEntity.ok(
                userService.updateName(user.getId(), payload)
        );
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> updatePassword(
            @Valid @RequestBody UpdatePasswordPayload payload,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        userService.updatePassword(user.getId(), payload);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/avatar")
    public ResponseEntity<UserDto> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ResponseEntity.ok(
                userService.updateAvatar(user.getId(), file)
        );
    }
}
