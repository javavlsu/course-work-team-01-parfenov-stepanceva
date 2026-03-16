package ru.ispi.kanban.mapper;

import org.mapstruct.Mapper;
import ru.ispi.kanban.dto.BoardDTO;
import ru.ispi.kanban.entity.Board;

@Mapper(componentModel = "spring")
public interface BoardMapper {
    BoardDTO toDto(Board board);
}