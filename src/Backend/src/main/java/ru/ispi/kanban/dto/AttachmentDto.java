package ru.ispi.kanban.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDto {
    private Integer id;
    private Integer taskId;
    private UserDto user;
    private String fileName;
    private String storageKey;
    private LocalDateTime uploadedAt;
}