package ru.ispi.kanban.services;

import org.springframework.web.multipart.MultipartFile;

//пока сделал просто интерфейс потому что может потом буду использовать какое нибудь S3 хранилище
public interface FileStorageService {

    String uploadFile(MultipartFile file, String folder);

    void deleteFile(String storageKey);

    byte[] downloadFile(String storageKey);

}