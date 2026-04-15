package ru.ispi.kanban.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.ispi.kanban.dto.UserDto;
import ru.ispi.kanban.entities.User;
import ru.ispi.kanban.exceptions.InvalidPasswordException;
import ru.ispi.kanban.exceptions.NoSuchUserByIdException;
import ru.ispi.kanban.exceptions.UserAlreadyExistsException;
import ru.ispi.kanban.exceptions.ValidationException;
import ru.ispi.kanban.mappers.UserMapper;
import ru.ispi.kanban.payloads.RegistrationPayload;
import ru.ispi.kanban.payloads.UpdateNamePayload;
import ru.ispi.kanban.payloads.UpdatePasswordPayload;
import ru.ispi.kanban.repositories.UserRepository;
import ru.ispi.kanban.services.impl.UserServiceImpl;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock UserRepository userRepository;

    @Mock PasswordEncoder passwordEncoder;

    @Mock UserMapper userMapper;

    @InjectMocks UserServiceImpl userService;

    @Test
    void create_success() {
        RegistrationPayload payload = new RegistrationPayload("test@mail.com", "TestUser", "password123");
        User user = new User();
        UserDto dto = new UserDto();

        when(userRepository.findByEmail(payload.email())).thenReturn(Optional.empty());
        when(userMapper.toEntity(payload)).thenReturn(user);
        when(passwordEncoder.encode(payload.password())).thenReturn("hashed");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(dto);

        UserDto result = userService.create(payload);

        assertThat(result).isSameAs(dto);
        verify(userRepository).save(user);
        assertThat(user.getPasswordHash()).isEqualTo("hashed");
    }

    @Test
    void create_emailAlreadyExists_throws() {
        RegistrationPayload payload = new RegistrationPayload("exists@mail.com", "Name", "password123");

        when(userRepository.findByEmail(payload.email())).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.create(payload))
                .isInstanceOf(UserAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void getById_success() {
        User user = new User();
        UserDto dto = new UserDto();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        assertThat(userService.getById(1)).isSameAs(dto);
    }

    @Test
    void getById_notFound_throws() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(99))
                .isInstanceOf(NoSuchUserByIdException.class);
    }

    @Test
    void updateName_success() {
        User user = new User();
        user.setName("OldName");
        UserDto dto = new UserDto();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(dto);

        UserDto result = userService.updateName(1, new UpdateNamePayload("NewName"));

        assertThat(result).isSameAs(dto);
        assertThat(user.getName()).isEqualTo("NewName");
    }

    @Test
    void updateName_blankName_throws() {
        User user = new User();
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.updateName(1, new UpdateNamePayload("   ")))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void updateName_nullName_throws() {
        User user = new User();
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.updateName(1, new UpdateNamePayload(null)))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void updatePassword_success() {
        User user = new User();
        user.setPasswordHash("oldHash");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass1", "oldHash")).thenReturn(true);
        when(passwordEncoder.encode("newPass123")).thenReturn("newHash");

        userService.updatePassword(1, new UpdatePasswordPayload("oldPass1", "newPass123"));

        assertThat(user.getPasswordHash()).isEqualTo("newHash");
        verify(userRepository).save(user);
    }

    @Test
    void updatePassword_wrongOldPassword_throws() {
        User user = new User();
        user.setPasswordHash("hash");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "hash")).thenReturn(false);

        assertThatThrownBy(() -> userService.updatePassword(1, new UpdatePasswordPayload("wrongPass", "newPass123")))
                .isInstanceOf(InvalidPasswordException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void updatePassword_newPasswordTooShort_throws() {
        User user = new User();
        user.setPasswordHash("hash");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass1", "hash")).thenReturn(true);

        assertThatThrownBy(() -> userService.updatePassword(1, new UpdatePasswordPayload("oldPass1", "short")))
                .isInstanceOf(ValidationException.class);
    }
}
