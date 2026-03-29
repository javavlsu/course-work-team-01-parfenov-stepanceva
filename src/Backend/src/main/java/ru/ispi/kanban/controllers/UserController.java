package ru.ispi.kanban.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ispi.kanban.dto.UserDto;
import ru.ispi.kanban.services.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kanban/users/")
public class UserController {

    private final UserService userService;

    @GetMapping()
    public ResponseEntity<List<UserDto>> getUsers() {
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("{id}")
    public UserDto getUserById(@PathVariable Integer id) {
        return userService.getById(id);
    }

    @GetMapping("email/{email}")
    public UserDto getUserByEmail(@PathVariable String email) {
        return userService.getByEmail(email);

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
