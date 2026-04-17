package ru.ispi.kanban.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    private static final Set<String> ALLOWED_FOLDERS = Set.of("avatars", "attachments");

    @Value("${spring.application.storage.location}")
    private String storageLocation;

    @GetMapping("/{folder}/{filename}")
    public ResponseEntity<Resource> getFile(
            @PathVariable String folder,
            @PathVariable String filename
    ) {
        if (!ALLOWED_FOLDERS.contains(folder)) {
            return ResponseEntity.notFound().build();
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
