package ru.ispi.kanban.services;

import ru.ispi.kanban.dto.ColumnDto;
import ru.ispi.kanban.payloads.CreateColumnPayload;
import ru.ispi.kanban.payloads.UpdateColumnPayload;

import java.util.List;

public interface ColumnService {

    List<ColumnDto> getColumns(Integer userId, Integer boardId);

    ColumnDto create(Integer userId, Integer boardId, CreateColumnPayload payload);

    ColumnDto update(Integer userId, Integer boardId, Integer columnId, UpdateColumnPayload payload);

    void delete(Integer userId, Integer boardId, Integer columnId);

    void move(Integer userId, Integer boardId, Integer columnId, Long newPosition);
}
