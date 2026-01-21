package com.recipesharing.service;

import com.recipesharing.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    private final Path storageRoot;
    private final String baseUrl;

    public LocalFileStorageService(
            @Value("${file.storage.location}") String storageLocation,
            @Value("${file.storage.base-url}") String baseUrl
    ) {
        this.storageRoot = Paths.get(storageLocation).toAbsolutePath().normalize();
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length()-1) : baseUrl;

        try {
            Files.createDirectories(this.storageRoot);
        } catch (IOException e) {
            throw new FileStorageException("Could not create storage directory", e);
        }
    }

    @Override
    public String store(byte[] data, String dirRelativePath, String filename) {
        // sanitize and build paths
        String safeDir = dirRelativePath == null || dirRelativePath.isBlank() ? "" : dirRelativePath.trim();
        Path targetDir = storageRoot.resolve(safeDir).normalize();

        try {
            Files.createDirectories(targetDir);
        } catch (IOException e) {
            throw new FileStorageException("Could not create directory for path: " + targetDir, e);
        }

        // Generate final filename with a UUID to avoid collisions
        String finalFilename = filename;
        if (finalFilename == null || finalFilename.isBlank()) {
            finalFilename = UUID.randomUUID().toString();
        } else {
            // append uuid to preserve uniqueness but keep original extension
            String ext = "";
            int i = finalFilename.lastIndexOf('.');
            if (i > 0) {
                ext = finalFilename.substring(i);
                finalFilename = finalFilename.substring(0, i);
            }
            finalFilename = finalFilename + "-" + UUID.randomUUID() + ext;
        }

        Path tempFile;
        try {
            tempFile = Files.createTempFile(targetDir, "upload-", ".tmp");
            Files.write(tempFile, data, StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new FileStorageException("Failed to write temp file", e);
        }

        // atomically move temp to final location
        Path finalPath = targetDir.resolve(finalFilename);
        try {
            Files.move(tempFile, finalPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException amnse) {
            try {
                Files.move(tempFile, finalPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                throw new FileStorageException("Failed to move file into final location", ex);
            }
        } catch (IOException e) {
            throw new FileStorageException("Failed to move file into final location", e);
        }

        // Build public URL: baseUrl + "/" + safeDir + "/" + finalFilename
        String relative = (safeDir.isEmpty() ? "" : (safeDir + "/")) + finalFilename;
        return baseUrl + "/" + relative.replace("\\", "/");
    }

    @Override
    public void delete(String publicUrlOrPath) {
        if (publicUrlOrPath == null || publicUrlOrPath.isBlank()) return;

        // Accept either full URL or relative path
        String relative;
        if (publicUrlOrPath.startsWith(baseUrl)) {
            relative = publicUrlOrPath.substring(baseUrl.length());
            if (relative.startsWith("/")) relative = relative.substring(1);
        } else {
            relative = publicUrlOrPath;
        }

        Path target = storageRoot.resolve(relative).normalize();
        try {
            // delete file if exists
            Files.deleteIfExists(target);

            // try cleaning empty parent directory (best-effort)
            Path parent = target.getParent();
            if (parent != null && Files.isDirectory(parent)) {
                try {
                    if (Files.list(parent).count() == 0) {
                        FileSystemUtils.deleteRecursively(parent);
                    }
                } catch (IOException ignored) { /* ignore */ }
            }
        } catch (IOException e) {
            throw new FileStorageException("Failed to delete stored file: " + target, e);
        }
    }
}
