package ru.ispi.kanban.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ispi.kanban.dto.ColumnDto;
import ru.ispi.kanban.entities.Board;
import ru.ispi.kanban.entities.BoardColumn;
import ru.ispi.kanban.mappers.ColumnMapper;
import ru.ispi.kanban.payloads.CreateColumnPayload;
import ru.ispi.kanban.payloads.UpdateColumnPayload;
import ru.ispi.kanban.repositories.BoardRepository;
import ru.ispi.kanban.repositories.BoardUserRepository;
import ru.ispi.kanban.exceptions.BoardAccessDeniedException;
import ru.ispi.kanban.exceptions.ColumnNotFoundException;
import ru.ispi.kanban.exceptions.EntityNotFound;
import ru.ispi.kanban.repositories.ColumnRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ColumnService {

    private final ColumnRepository columnRepository;

    private final BoardRepository boardRepository;

    private final BoardUserRepository boardUserRepository;

    private final ColumnMapper columnMapper;

    public List<ColumnDto> getColumns(Integer userId, Integer boardId) {
        checkAccess(userId, boardId);

        return columnRepository.findAllByBoardIdOrderByPositionAsc(boardId)
                .stream()
                .map(columnMapper::toDto)
                .toList();
    }

    @Transactional
    public ColumnDto create(Integer userId, Integer boardId, CreateColumnPayload payload) {
        checkAccess(userId, boardId);

        Board board = getBoard(boardId);

        Long maxPosition = columnRepository.findMaxPosition(boardId);
        Long newPosition = maxPosition + 1;

        BoardColumn column = columnMapper.toEntity(payload);
        column.setBoard(board);
        column.setPosition(newPosition);

        return columnMapper.toDto(columnRepository.save(column));
    }

    @Transactional
    public ColumnDto update(Integer userId, Integer boardId, Integer columnId, UpdateColumnPayload payload) {
        checkAccess(userId, boardId);

        BoardColumn column = getColumn(boardId, columnId);

        Long oldPosition = column.getPosition();

        if (payload.position() != null && !payload.position().equals(oldPosition)) {
            move(userId, boardId, columnId, payload.position());
        }

        columnMapper.update(column, payload);

        return columnMapper.toDto(column);
    }

    @Transactional
    public void delete(Integer userId, Integer boardId, Integer columnId) {

        checkAccess(userId, boardId);

        BoardColumn column = getColumn(boardId, columnId);
        Long deletedPosition = column.getPosition();

        columnRepository.delete(column);

        // сдвигаем все колонки, которые стояли после удаленной, на -1
        columnRepository.shiftAfterDelete(boardId, deletedPosition);
    }

    @Transactional
    public void move(Integer userId, Integer boardId, Integer columnId, Long newPosition) {
        checkAccess(userId, boardId);
        BoardColumn column = getColumn(boardId, columnId);
        Long oldPosition = column.getPosition();

        Long maxPosition = columnRepository.findMaxPosition(boardId);
        // Валидация границ
        if (newPosition > maxPosition) newPosition = maxPosition; //мб тут сделать выброс исключения
        if (newPosition < 1) newPosition = 1L;

        if (oldPosition.equals(newPosition)) return;

        if (oldPosition < newPosition) {
            columnRepository.shiftForward(boardId, oldPosition, newPosition);
        } else {
            columnRepository.shiftBackward(boardId, oldPosition, newPosition);
        }

        column.setPosition(newPosition);
        columnRepository.save(column);
    }


    private void checkAccess(Integer userId, Integer boardId) {
        if (!boardUserRepository.existsByIdBoardIdAndIdUserId(boardId, userId)) {
            throw new BoardAccessDeniedException("No access to this board");
        }
    }

    private Board getBoard(Integer boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFound("Board not found: " + boardId));
    }

    private BoardColumn getColumn(Integer boardId, Integer columnId) {
        return columnRepository.findByIdAndBoardId(columnId, boardId)
                .orElseThrow(() -> new ColumnNotFoundException("Column not found: " + columnId));
    }
}