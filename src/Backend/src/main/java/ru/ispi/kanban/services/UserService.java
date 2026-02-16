package ru.ispi.kanban.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ispi.kanban.dto.UserDTO;
import ru.ispi.kanban.entity.User;
import ru.ispi.kanban.payload.UserPayload;
import ru.ispi.kanban.repository.UserRepository;
import ru.ispi.kanban.util.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDTO create(UserPayload payload) {
        // Проверяем, не существует ли уже пользователь с таким email
        if (userRepository.FindByEmail(payload.email()).isPresent()) {
            throw new IllegalArgumentException("User with email " + payload.email() + " already exists");
        }

        User user = new User();
        user.setEmail(payload.email());
        user.setName(payload.name());
        // Хеш
        user.setPasswordHash(PasswordEncoder.hashPassword(payload.password()));
        user.setAvatarUrl(payload.avatarUrl());
        user.setCreatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public List<UserDTO> getAll() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserDTO> getById(Integer id) {
        return userRepository.FindById(id)
                .map(this::convertToDTO);
    }

    public Optional<UserDTO> getByEmail(String email) {
        return userRepository.FindByEmail(email)
                .map(this::convertToDTO);
    }

    public UserDTO update(Integer id, UserPayload payload) {
        User user = userRepository.FindById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));

        // Проверяем, не занят ли email другим пользователем
        Optional<User> existingUser = userRepository.FindByEmail(payload.email());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
            throw new IllegalArgumentException("User with email " + payload.email() + " already exists");
        }

        user.setEmail(payload.email());
        user.setName(payload.name());
        // Хешируем пароль только если он был передан
        if (payload.password() != null && !payload.password().isEmpty()) {
            user.setPasswordHash(PasswordEncoder.hashPassword(payload.password()));
        }
        user.setAvatarUrl(payload.avatarUrl());

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    public void deleteById(Integer id) {
        if (userRepository.FindById(id).isEmpty()) {
            throw new IllegalArgumentException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

}
