package ru.ispi.kanban.services;

import ru.ispi.kanban.dto.BoardDto;
import ru.ispi.kanban.payloads.CreateBoardPayload;
import ru.ispi.kanban.payloads.UpdateBoardPayload;

import java.util.List;

public interface BoardService {

    List<BoardDto> getUserBoards(Integer userId);

    BoardDto getUserBoard(Integer userId, Integer boardId);

    BoardDto create(CreateBoardPayload payload, Integer userId);

    BoardDto update(UpdateBoardPayload payload, Integer userId, Integer boardId);

    void delete(Integer userId, Integer boardId);

    List<BoardDto> getUserBoardsInGroup(Integer userId, Integer groupId);
}
