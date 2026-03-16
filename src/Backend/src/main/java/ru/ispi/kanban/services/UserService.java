package ru.ispi.kanban.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ispi.kanban.dto.UserDTO;
import ru.ispi.kanban.entity.User;
import ru.ispi.kanban.exceptions.NoSuchUserByEmailException;
import ru.ispi.kanban.exceptions.NoSuchUserByIdException;
import ru.ispi.kanban.mapper.UserMapper;
import ru.ispi.kanban.payload.RegistrationPayload;
import ru.ispi.kanban.payload.UserPayload;
import ru.ispi.kanban.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    public UserDTO create(RegistrationPayload payload) {
        // Проверяем, не существует ли уже пользователь с таким email
        if (userRepository.findByEmail(payload.email()).isPresent()) {
            throw new IllegalArgumentException("User with email " + payload.email() + " already exists");
        }

        User user = new User();
        user.setEmail(payload.email());
        user.setName(payload.name());
        // Хеш
        user.setPasswordHash(passwordEncoder.encode(payload.password()));
        user.setAvatarUrl(null);
        
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public List<UserDTO> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserDTO getById(Integer id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new NoSuchUserByIdException(String.format("User with id %s does not exist", id)));
    }

    public UserDTO getByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> new NoSuchUserByEmailException(String.format("User by %s not found", email)));
    }

    public UserDTO update(Integer id, UserPayload payload) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));

        // Проверяем, не занят ли email другим пользователем
        Optional<User> existingUser = userRepository.findByEmail(payload.email());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
            throw new IllegalArgumentException("User with email " + payload.email() + " already exists");
        }

        user.setEmail(payload.email());
        user.setName(payload.name());
        // Хешируем пароль только если он был передан
        if (payload.password() != null && !payload.password().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(payload.password()));
        }
        user.setAvatarUrl(payload.avatarUrl());

        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    public void deleteById(Integer id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    public User getEntity(Integer id){
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
