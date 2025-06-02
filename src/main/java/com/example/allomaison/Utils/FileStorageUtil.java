package com.example.allomaison.Utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class FileStorageUtil {

    private static final String AVATAR_STORAGE_PATH = "src/main/resources/static/avatars/";
    private static final String AVATAR_URL_PREFIX = "/static/avatars/";

    private static final String FILE_STORAGE_PATH = "src/main/resources/static/files/";
    private static final String FILE_URL_PREFIX = "/static/files/";

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "application/pdf"
    );

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".jpg", ".jpeg", ".png", ".gif", ".pdf"
    );

    public static FileUploadResult saveAvatarFile(MultipartFile file, Long userId) {
        String prefix = "user_" + userId;
        return saveFile(file, AVATAR_STORAGE_PATH, AVATAR_URL_PREFIX, prefix);
    }

    public static FileUploadResult saveGeneralFile(MultipartFile file, String prefix) {
        return saveFile(file, FILE_STORAGE_PATH, FILE_URL_PREFIX, prefix);
    }

    private static FileUploadResult saveFile(MultipartFile file, String path, String urlPrefix, String prefix) {
        if (file == null || file.isEmpty()) {
            return new FileUploadResult(false, null, "File is empty.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return new FileUploadResult(false, null, "File too large. Max allowed size is 10MB.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            return new FileUploadResult(false, null, "Unsupported file type: " + contentType);
        }

        String extension = getExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            return new FileUploadResult(false, null, "Invalid file extension: " + extension);
        }

        String filename = prefix + "_" + UUID.randomUUID() + extension;
        File dest = new File(path + filename);
        File dir = dest.getParentFile();
        if (!dir.exists() && !dir.mkdirs()) {
            return new FileUploadResult(false, null, "Failed to create directory.");
        }

        try {
            file.transferTo(dest);
        } catch (IOException e) {
            return new FileUploadResult(false, null, "File write error: " + e.getMessage());
        }

        return new FileUploadResult(true, urlPrefix + filename, null);
    }

    private static String getExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        return (dot >= 0) ? filename.substring(dot) : "";
    }
}
