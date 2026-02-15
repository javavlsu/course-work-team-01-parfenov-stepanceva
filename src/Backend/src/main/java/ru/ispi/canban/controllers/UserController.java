package ru.ispi.canban.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ispi.canban.dto.UserDTO;
import ru.ispi.canban.payload.UserPayload;
import ru.ispi.canban.services.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/canban/user/")
public class UserController {

    private final UserService userService;

    @GetMapping("hel")
    public String helloworld(){
        return "hello world!";
    }

    @GetMapping()
    public ResponseEntity<?> getUsers(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String email) {
        
        // Если передан id, ищем по id
        if (id != null) {
            Optional<UserDTO> user = userService.getById(id);
            return user.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
        
        // Если передан email, ищем по email
        if (email != null) {
            Optional<UserDTO> user = userService.getByEmail(email);
            return user.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
        
        // Если параметры не переданы, возвращаем всех пользователей
        return ResponseEntity.ok(userService.getAll());
    }

    @PostMapping()
    public ResponseEntity<UserDTO> create(@RequestBody UserPayload userPayload) {
        try {
            UserDTO createdUser = userService.create(userPayload);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping()
    public ResponseEntity<UserDTO> update(@RequestParam Integer id, @RequestBody UserPayload userPayload) {
        try {
            UserDTO updatedUser = userService.update(id, userPayload);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping()
    public ResponseEntity<Void> delete(@RequestParam Integer id) {
        try {
            userService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
