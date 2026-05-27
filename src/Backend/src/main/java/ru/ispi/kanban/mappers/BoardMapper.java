package ru.ispi.kanban.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.ispi.kanban.dto.BoardDto;
import ru.ispi.kanban.entities.Board;
import ru.ispi.kanban.payloads.CreateBoardPayload;
import ru.ispi.kanban.payloads.UpdateBoardPayload;

@Mapper(componentModel = "spring")
public interface BoardMapper {
    BoardDto toDto(Board board);

    Board toEntity(CreateBoardPayload payload);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Board board, UpdateBoardPayload payload);
}