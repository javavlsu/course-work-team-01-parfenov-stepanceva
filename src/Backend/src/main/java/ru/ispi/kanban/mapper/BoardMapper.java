package ru.ispi.kanban.mapper;

import org.mapstruct.Mapper;
import ru.ispi.kanban.dto.BoardDto;
import ru.ispi.kanban.entity.Board;

@Mapper(componentModel = "spring")
public interface BoardMapper {
    BoardDto toDto(Board board);
}