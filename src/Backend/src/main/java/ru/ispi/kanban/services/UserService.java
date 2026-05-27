package ru.ispi.kanban.services;

import org.springframework.web.multipart.MultipartFile;
import ru.ispi.kanban.dto.UserDto;
import ru.ispi.kanban.entities.User;
import ru.ispi.kanban.payloads.RegistrationPayload;
import ru.ispi.kanban.payloads.UpdateNamePayload;
import ru.ispi.kanban.payloads.UpdatePasswordPayload;

import java.util.List;

public interface UserService {

    UserDto create(RegistrationPayload payload);

    List<UserDto> getAll();

    UserDto getById(Integer id);

    UserDto getByEmail(String email);

    User getEntity(Integer id);

    UserDto updateName(Integer id, UpdateNamePayload payload);

    void updatePassword(Integer id, UpdatePasswordPayload payload);

    UserDto updateAvatar(Integer id, MultipartFile file);
}
