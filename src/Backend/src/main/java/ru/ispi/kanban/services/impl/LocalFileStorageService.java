package ru.ispi.kanban.services.impl;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.ispi.kanban.exceptions.FileEmptyException;
import ru.ispi.kanban.exceptions.FileStorageException;
import ru.ispi.kanban.exceptions.InvalidFilePathException;
import ru.ispi.kanban.services.FileStorageService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class LocalFileStorageService implements FileStorageService {

    private final Path rootLocation;

    public LocalFileStorageService(@Value("${spring.application.storage.location}") String storageLocation) {
        this.rootLocation = Paths.get(storageLocation);
    }

    // создать локальные папки
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
            Files.createDirectories(rootLocation.resolve("attachments"));
            Files.createDirectories(rootLocation.resolve("avatars"));
            log.info("Директории для файлов инициализированы: {}", rootLocation.toAbsolutePath());
        } catch (IOException e) {
            throw new FileStorageException("Не удалось создать директории для хранения файлов", e);
        }
    }

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        if (file.isEmpty()) {
            throw new FileEmptyException("Файл пуст");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = StringUtils.getFilenameExtension(originalFilename);

        String generatedFileName = UUID.randomUUID().toString() + (extension != null ? "." + extension : "");

        Path destinationFolder = this.rootLocation.resolve(folder).normalize();
        Path destinationFile = destinationFolder.resolve(generatedFileName);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileStorageException("Ошибка при сохранении файла", e);
        }

        return folder + "/" + generatedFileName;
    }

    @Override
    public void deleteFile(String storageKey) {
        if (storageKey == null || storageKey.isEmpty()) return;

        try {
            Path fileToDelete = rootLocation.resolve(storageKey).normalize();
            Files.deleteIfExists(fileToDelete);
            log.info("Файл удален: {}", storageKey);
        } catch (IOException e) {
            log.error("Ошибка при удалении файла: {}", storageKey, e);
        }
    }

    @Override
    public byte[] downloadFile(String storageKey) {
        try {
            Path filePath = rootLocation.resolve(storageKey).normalize();
            if (!filePath.startsWith(rootLocation.normalize())) {
                throw new InvalidFilePathException("Недопустимый путь к файлу");
            }
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new FileStorageException("Ошибка при чтении файла: " + storageKey, e);
        }
    }
}
