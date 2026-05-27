package ru.ispi.kanban.listeners;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import ru.ispi.kanban.entities.User;
import ru.ispi.kanban.services.UserService;
import ru.ispi.kanban.utils.SecurityUtils;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<User> {

    private final UserService userService;

    @Override
    public Optional<User> getCurrentAuditor() {
        Integer id = SecurityUtils.getCurrentUserId();
        return id == null ? Optional.empty() : Optional.of(userService.getEntity(id));
    }
}