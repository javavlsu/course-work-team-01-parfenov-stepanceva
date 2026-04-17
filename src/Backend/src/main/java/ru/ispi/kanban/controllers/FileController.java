package ru.ispi.kanban.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.ispi.kanban.repositories.AttachmentRepository;
import ru.ispi.kanban.repositories.BoardUserRepository;
import ru.ispi.kanban.security.CustomUserDetails;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

import static ru.ispi.kanban.constants.StorageConstants.FOLDER_ATTACHMENTS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    private static final Set<String> ALLOWED_FOLDERS = Set.of("avatars", "attachments");

    @Value("${spring.application.storage.location}")
    private String storageLocation;

    private final AttachmentRepository attachmentRepository;
    private final BoardUserRepository boardUserRepository;

    @GetMapping("/{folder}/{filename}")
    public ResponseEntity<Resource> getFile(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String folder,
            @PathVariable String filename
    ) {
        if (!ALLOWED_FOLDERS.contains(folder)) {
            return ResponseEntity.notFound().build();
        }

        // Вложения доступны только членам доски, которой принадлежит файл
        if (FOLDER_ATTACHMENTS.equals(folder)) {
            Optional<Integer> boardId = attachmentRepository.findBoardIdByStorageKey(filename);
            if (boardId.isEmpty() || !boardUserRepository.existsByIdBoardIdAndIdUserId(boardId.get(), user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        try {
            Path root = Paths.get(storageLocation).toAbsolutePath().normalize();
            Path filePath = root.resolve(folder).resolve(filename).normalize();

            if (!filePath.startsWith(root)) {
                return ResponseEntity.badRequest().build();
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
