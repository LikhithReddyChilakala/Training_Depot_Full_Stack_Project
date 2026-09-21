package com.trainingdepot.gear.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");

    private final String uploadDir;

    public ImageStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.uploadDir = uploadDir;
    }

    /**
     * Stores the file under a generated name (never the client-supplied
     * filename) so a crafted "../../file" name can't escape the upload
     * directory and two uploads can never silently overwrite each other.
     * Returns null when no file was actually chosen.
     */
    public String store(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalName = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String extension = extensionOf(originalName);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Only JPG, PNG, or WEBP images are accepted");
        }

        Path directory = Paths.get(uploadDir);
        Files.createDirectories(directory);

        String generatedName = UUID.randomUUID() + "." + extension;
        Path target = directory.resolve(generatedName).normalize();
        file.transferTo(target);
        return generatedName;
    }

    private String extensionOf(String filename) {
        List<String> parts = List.of(filename.split("\\."));
        if (parts.size() < 2) {
            return "";
        }
        return parts.get(parts.size() - 1).toLowerCase();
    }
}
