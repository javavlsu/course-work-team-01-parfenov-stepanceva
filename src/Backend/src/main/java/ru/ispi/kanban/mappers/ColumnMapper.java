package ru.ispi.kanban.mappers;


import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.ispi.kanban.dto.ColumnDto;
import ru.ispi.kanban.entities.BoardColumn;
import ru.ispi.kanban.payloads.CreateColumnPayload;
import ru.ispi.kanban.payloads.UpdateColumnPayload;

@Mapper(componentModel = "spring")
public interface ColumnMapper {

    // сделал мапинг для того чтобы выдавал айди доски, а не полную сущность доски, т.к. слишком перегруз ответа получается
    @Mapping(target = "boardId", source = "board.id")
    ColumnDto toDto(BoardColumn boardColumn);


    BoardColumn toEntity(CreateColumnPayload payload);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget BoardColumn column, UpdateColumnPayload payload);
}
