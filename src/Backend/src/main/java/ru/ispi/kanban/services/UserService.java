package ru.ispi.kanban.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.ispi.kanban.dto.UserDto;
import ru.ispi.kanban.entities.User;
import ru.ispi.kanban.exceptions.NoSuchUserByEmailException;
import ru.ispi.kanban.exceptions.NoSuchUserByIdException;
import ru.ispi.kanban.mappers.UserMapper;
import ru.ispi.kanban.payloads.RegistrationPayload;
import ru.ispi.kanban.payloads.UpdateNamePayload;
import ru.ispi.kanban.payloads.UpdatePasswordPayload;
import ru.ispi.kanban.repositories.UserRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.ispi.kanban.constants.StorageConstants.FOLDER_AVATARS;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    private final LocalFileStorageService localFileStorageService;

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public UserDto create(RegistrationPayload payload) {
        // Проверяем, не существует ли уже пользователь с таким email
        if (userRepository.findByEmail(payload.email()).isPresent()) {
            throw new IllegalArgumentException("User with email " + payload.email() + " already exists");
        }

        User user = userMapper.toEntity(payload);
        user.setPasswordHash(passwordEncoder.encode(payload.password()));
        user.setAvatarUrl(null);
        
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserDto getById(Integer id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new NoSuchUserByIdException(String.format("User with id %s does not exist", id)));
    }

    public UserDto getByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> new NoSuchUserByEmailException(String.format("User by %s not found", email)));
    }

    public User getEntity(Integer id){ return userRepository.findById(id) .orElseThrow(() -> new RuntimeException("User not found")); }

    @Transactional
    public UserDto updateName(Integer id, UpdateNamePayload payload) {
        User user = getEntity(id);

        if (payload.name() == null || payload.name().isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        user.setName(payload.name().trim());

        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public void updatePassword(Integer id, UpdatePasswordPayload payload) {
        User user = getEntity(id);


        if (!passwordEncoder.matches(payload.oldPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid old password");
        }

        if (payload.newPassword() == null || payload.newPassword().length() < 6) {
            throw new IllegalArgumentException("Password too short");
        }

        user.setPasswordHash(passwordEncoder.encode(payload.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public UserDto updateAvatar(Integer id, MultipartFile file) {
        User user = getEntity(id);

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File too large");
        }

        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Invalid file type");
        }

        // удаляем старый файл
        if (user.getAvatarUrl() != null) {
            localFileStorageService.deleteFile(user.getAvatarUrl());
        }

        String storageKey = localFileStorageService.uploadFile(file, FOLDER_AVATARS);

        user.setAvatarUrl(storageKey);

        return userMapper.toDto(userRepository.save(user));
    }
}
