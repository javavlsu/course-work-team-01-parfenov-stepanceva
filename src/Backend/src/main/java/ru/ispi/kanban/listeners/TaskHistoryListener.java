package ru.ispi.kanban.listeners;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.ispi.kanban.entities.User;
import ru.ispi.kanban.repositories.UserRepository;
import ru.ispi.kanban.services.TaskHistoryService;

@Component
@RequiredArgsConstructor
public class TaskHistoryListener {

    private final TaskHistoryService taskHistoryService;

    private final UserRepository userRepository;

    @EventListener
    public void handleTaskChangeEvent(TaskChangeEvent event) {

        User user = userRepository.findById(event.userId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        taskHistoryService.createHistoryRecord(
                event.task(),
                user,
                event.actionType(),
                event.changedAttribute(),
                event.oldValue(),
                event.newValue()
        );
    }
}