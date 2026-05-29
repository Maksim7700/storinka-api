package ua.storinka.backend.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Local-disk implementation of file storage. MVP-only — when we move to prod
 * this will be swapped for an S3 / R2 implementation behind the same surface
 * (saveImage / load). Callers should only depend on the returned URL string
 * and never assume on-disk paths.
 */
@Service
@Slf4j
public class FileStorageService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );

    /** Max file size enforced at the service layer (Spring multipart limit is
     *  configured separately in application.yml and acts as a hard cap). */
    private static final long MAX_BYTES = 5L * 1024 * 1024; // 5 MB

    private final Path uploadsDir;

    public FileStorageService(@Value("${app.uploads.dir:./uploads}") String uploadsDir) {
        this.uploadsDir = Path.of(uploadsDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    void init() {
        try {
            Files.createDirectories(uploadsDir);
            log.info("FileStorageService initialised with dir={}", uploadsDir);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create uploads dir " + uploadsDir, e);
        }
    }

    /** Persist an uploaded image and return its public URL (relative to the
     *  backend root, e.g. "/api/files/3f8c...png"). */
    public String saveImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "File is empty");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new ResponseStatusException(BAD_REQUEST, "File too large (max 5 MB)");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "Unsupported file type. Allowed: " + ALLOWED_TYPES);
        }

        String ext = extensionFor(contentType);
        String filename = UUID.randomUUID() + ext;
        Path target = uploadsDir.resolve(filename).normalize();

        // Defensive: ensure we never escape the uploads dir (path traversal).
        if (!target.startsWith(uploadsDir)) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid filename");
        }

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Failed to store file", e);
        }
        return "/api/files/" + filename;
    }

    /** Load a previously-stored file by its filename (last URL segment). */
    public Resource load(String filename) {
        // Reject any filename containing path separators — only flat names live
        // in the uploads dir.
        if (filename.contains("/") || filename.contains("\\") || filename.contains("..")) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid filename");
        }
        Path target = uploadsDir.resolve(filename).normalize();
        if (!target.startsWith(uploadsDir) || !Files.isRegularFile(target)) {
            throw new ResponseStatusException(NOT_FOUND, "File not found");
        }
        return new FileSystemResource(target);
    }

    private static String extensionFor(String contentType) {
        return switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/jpeg" -> ".jpg";
            case "image/png"  -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif"  -> ".gif";
            default           -> "";
        };
    }
}
