package ru.ispi.kanban.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ispi.kanban.dto.UserDTO;
import ru.ispi.kanban.payload.UserPayload;
import ru.ispi.kanban.services.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kanban/user/")
public class UserController {

    private final UserService userService;

    @GetMapping()
    public ResponseEntity<List<UserDTO>> getUsers() {
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Integer id) {
        return userService.getById(id)
                .map(u -> ResponseEntity.ok(u))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        return userService.getByEmail(email)
                .map(u -> ResponseEntity.ok(u))
                .orElse(ResponseEntity.notFound().build());
    }

//    @PostMapping()
//    public ResponseEntity<UserDTO> create(@RequestBody UserPayload userPayload) {
//        try {
//            UserDTO createdUser = userService.create(userPayload);
//            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//        }
//    }
//
//    @PutMapping()
//    public ResponseEntity<UserDTO> update(@RequestParam Integer id, @RequestBody UserPayload userPayload) {
//        try {
//            UserDTO updatedUser = userService.update(id, userPayload);
//            return ResponseEntity.ok(updatedUser);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//        }
//    }
//
//    @DeleteMapping()
//    public ResponseEntity<Void> delete(@RequestParam Integer id) {
//        try {
//            userService.deleteById(id);
//            return ResponseEntity.noContent().build();
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
}
